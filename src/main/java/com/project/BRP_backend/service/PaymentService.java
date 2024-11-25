package com.project.BRP_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.BRP_backend.domain.payment.*;
import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.exception.AppException;
import com.project.BRP_backend.model.constants.PaymentStatus;
import com.project.BRP_backend.model.payment.Payment;
import com.project.BRP_backend.model.product.Product;
import com.project.BRP_backend.model.user.User;
import com.project.BRP_backend.repository.payment.PaymentRepository;
import com.project.BRP_backend.repository.product.ProductRepository;
import com.project.BRP_backend.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private User user;
    private final ResourceLoader resourceLoader;

    public PaymentService(PaymentRepository paymentRepository,
                          ProductRepository productRepository,
                          UserRepository userRepository,
                          @Qualifier("webApplicationContext") ResourceLoader resourceLoader,
                          @Value("${payment.api.key}") String payment_api_key,
                          @Value("${server.ssl.key-store-password}") String keyStorePassword,
                          @Value("${server.ssl.trust-store-password}") String trustStorePassword) throws Exception {
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.resourceLoader = resourceLoader;
        this.payment_api_key = payment_api_key;
        this.keyStorePassword = keyStorePassword;
        this.trustStorePassword = trustStorePassword;
        sslContext = configureSSL();
    }


    public ResponseDetails initializePayment(Cart cart) {
        InitializeTransactionData data = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String paymentId = "";
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseDetails(LocalDateTime.now(), "Not Authenticated", HttpStatus.EXPECTATION_FAILED.toString());
        }
        user = userRepository.findByEmail(authentication.getName());
        List<Product> products = cart.getProductAndQuantityList()
                .parallelStream()
                .map(productAndQuantity -> productRepository.findById(productAndQuantity.getProductId())
                        .orElseThrow(() -> new AppException("Product doesn't exist")))
                .collect(Collectors.toList());

        int amount = cart.getProductAndQuantityList()
                .parallelStream()
                .map(productAndQuantity ->
                    productRepository.findById(productAndQuantity.getProductId())
                            .orElseThrow(() -> new AppException("Product doesn't exist"))
                            .getPrice() * productAndQuantity.getQuantity()
                ).reduce(0, Integer::sum);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InitializeTransaction initializeTransaction = InitializeTransaction.builder()
                    .amount(String.valueOf(amount))
                    .email(authentication.getName())
                    .reference(cart.getId())
                    .callback_url("") //TODO: set callback url
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
            Payment payment = Payment.builder()
                    .paymentStatus(PaymentStatus.PENDING)
                    .totalPaymentAmount(amount)
                    .products(products)
                    .userId(user.getId())
                    .build();
            payment = paymentRepository.save(payment);
            paymentId = payment.getId();

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new AppException(e.getMessage());
        }

        return new ResponseDetails(LocalDateTime.now(), "Payment Successful", HttpStatus.OK.toString(), Map.of("data",data, "payment_id",paymentId));
    }

    public ResponseDetails verifyPayment(String reference, String paymentId) {
        VerifyTransactionData data = null;
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
            data = verifyTransactionResponse.getData();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new AppException(e.getMessage());
        }
        return new ResponseDetails(LocalDateTime.now(), "Verification", HttpStatus.OK.toString(), Map.of("data", data));
    }

    private SSLContext configureSSL() throws Exception{
        Resource keystoreResource = resourceLoader.getResource("classpath:keystore.jks");
        Resource truststoreResource = resourceLoader.getResource("classpath:keystore.jks");

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
}
