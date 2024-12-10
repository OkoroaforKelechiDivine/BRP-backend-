package com.project.BRP_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.BRP_backend.domain.payment.*;
import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.event.EventType;
import com.project.BRP_backend.event.UserEvent;
import com.project.BRP_backend.exception.AppException;
import com.project.BRP_backend.model.constants.PaymentStatus;
import com.project.BRP_backend.model.payment.Payment;
import com.project.BRP_backend.model.product.Product;
import com.project.BRP_backend.repository.payment.PaymentRepository;
import com.project.BRP_backend.repository.product.ProductRepository;
import com.project.BRP_backend.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.BRP_backend.model.constants.PaymentConstants.INITIALIZE_TRANSACTION_URL;
import static com.project.BRP_backend.model.constants.PaymentConstants.VERIFY_TRANSACTION_URL;

@Service
public class PaymentService {


    private final String payment_api_key;

    private final String keyStorePassword;

    private final String trustStorePassword;

    private final SSLContext sslContext;

    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ResourceLoader resourceLoader;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentService(PaymentRepository paymentRepository,
                          ProductRepository productRepository,
                          UserRepository userRepository,
                          @Qualifier("webApplicationContext") ResourceLoader resourceLoader,
                          @Value("${payment.api.key}") String payment_api_key,
                          @Value("${server.ssl.key-store-password}") String keyStorePassword,
                          @Value("${server.ssl.trust-store-password}") String trustStorePassword, ApplicationEventPublisher eventPublisher) throws Exception {
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.resourceLoader = resourceLoader;
        this.payment_api_key = payment_api_key;
        this.keyStorePassword = keyStorePassword;
        this.trustStorePassword = trustStorePassword;
        this.eventPublisher = eventPublisher;
        sslContext = configureSSL();
    }


    public ResponseDetails initializePayment(Cart cart) {
        InitializeTransactionData data = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String paymentId = "";
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseDetails(LocalDateTime.now(), "Not Authenticated", HttpStatus.UNAUTHORIZED.toString());
        }
        UserDetails user = getCurrentUser();
        List<Product> products = cart.getProductAndQuantityList()
                .parallelStream()
                .map(productAndQuantity -> productRepository.findById(productAndQuantity.getProductId())
                        .orElseThrow(() -> new AppException("Product doesn't exist")))
                .collect(Collectors.toList());

        int amountInBaseUnit = cart.getProductAndQuantityList()
                .parallelStream()
                .map(productAndQuantity ->
                    productRepository.findById(productAndQuantity.getProductId())
                            .orElseThrow(() -> new AppException("Product doesn't exist"))
                            .getPrice() * 100 * productAndQuantity.getQuantity()
                ).reduce(0, Integer::sum);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InitializeTransaction initializeTransaction = InitializeTransaction.builder()
                    .amount(String.valueOf(amountInBaseUnit))
                    .email(authentication.getName())
                    .reference(cart.getId())
                    .currency("NGN")
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(INITIALIZE_TRANSACTION_URL))
                    .version(HttpClient.Version.HTTP_2)
                    .headers("authorization", payment_api_key,"Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(initializeTransaction)))
                    .build();

            HttpClient httpClient = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            InitializeTransactionResponse initializeTransactionResponse = objectMapper.readValue(response.body(), InitializeTransactionResponse.class);
            data = initializeTransactionResponse.getData();
            if (initializeTransactionResponse.getStatus().equals("true")) {
                var appUser = userRepository.findByEmail(user.getUsername());
                Payment payment = Payment.builder()
                        .paymentStatus(PaymentStatus.PENDING)
                        .totalPaymentAmount(amountInBaseUnit)
                        .products(products)
                        .userId(appUser.getId())
                        .build();
                payment = paymentRepository.save(payment);
                paymentId = payment.getId();
                UserEvent userEvent = new UserEvent(appUser, EventType.PAYMENT_INITIALIZED,Map.of("payment_id", paymentId));
                eventPublisher.publishEvent(userEvent);
                return new ResponseDetails(LocalDateTime.now(), "Payment Initialization Successful", HttpStatus.OK.toString(), Map.of("data",data, "payment_id",paymentId));
            }
            return new ResponseDetails(LocalDateTime.now(), "Payment Initialization Failed, Please try again", "failed", Map.of());


        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new AppException(e.getMessage());
        }


    }

    public ResponseDetails verifyPayment(String reference, String paymentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseDetails(LocalDateTime.now(), "Not Authenticated", HttpStatus.UNAUTHORIZED.toString());
        }
        UserDetails user = getCurrentUser();
        VerifyTransactionData data = null;
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new AppException("Payment not found"));
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(VERIFY_TRANSACTION_URL+reference))
                    .version(HttpClient.Version.HTTP_2)
                    .headers("authorization", payment_api_key, "Content-Type", "application/json")
                    .GET()
                    .build();
            HttpClient httpClient = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .version(HttpClient.Version.HTTP_2)
                    .build();
            HttpResponse<String> response  = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            VerifyTransactionResponse verifyTransactionResponse = objectMapper.convertValue(response, VerifyTransactionResponse.class);
            if (response.statusCode() == 200) {
                data = verifyTransactionResponse.getData();
                if (data.getStatus().equals("success")) {
                    var appUser = userRepository.findByEmail(user.getUsername());
                    payment.setPaymentStatus(PaymentStatus.SUCCESS);
                    UserEvent userEvent = new UserEvent(appUser, EventType.PAYMENT_SUCCESS,Map.of("payment_id", paymentId));
                    paymentRepository.save(payment);
                    eventPublisher.publishEvent(userEvent);
                } else if (data.getStatus().equals("failed")) {
                    var appUser = userRepository.findByEmail(user.getUsername());
                    payment.setPaymentStatus(PaymentStatus.FAILED);
                    UserEvent userEvent = new UserEvent(appUser, EventType.PAYMENT_FAILED,Map.of("payment_id", paymentId));
                    paymentRepository.save(payment);
                    eventPublisher.publishEvent(userEvent);
                }
                return new ResponseDetails(LocalDateTime.now(), "Verification", HttpStatus.OK.toString(), Map.of("data", data));
            } else {
                return new ResponseDetails(LocalDateTime.now(), "Verification failed "+ verifyTransactionResponse.getMessage(), HttpStatus.BAD_REQUEST.toString(), Map.of());
            }


        } catch (URISyntaxException | IOException | InterruptedException | AppException e) {
            if (e instanceof AppException ) {
                return new ResponseDetails(LocalDateTime.now(), e.getMessage(), "unsuccessful", Map.of());
            }
            return new ResponseDetails(LocalDateTime.now(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), Map.of());
        }

    }

    private SSLContext configureSSL() throws Exception{
        Resource keystoreResource = resourceLoader.getResource("classpath:keystore.jks");
        Resource truststoreResource = resourceLoader.getResource("classpath:truststore.jks");

        Path keystorePath = keystoreResource.getFile().toPath();
        Path truststorePath = truststoreResource.getFile().toPath();

        KeyStore keyStore = KeyStore.getInstance("JKS");
        KeyStore trustStore = KeyStore.getInstance("JKS");

        keyStore.load(Files.newInputStream(keystorePath), keyStorePassword.toCharArray());
        trustStore.load(Files.newInputStream(truststorePath), trustStorePassword.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore,keyStorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }

    private UserDetails getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) authentication.getPrincipal();
    }
}
