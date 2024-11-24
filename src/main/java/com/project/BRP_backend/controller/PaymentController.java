package com.project.BRP_backend.controller;

import com.project.BRP_backend.domain.payment.Cart;
import com.project.BRP_backend.dto.response.ResponseDetails;
import com.project.BRP_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("initialize")
    @Async
    public Future<ResponseDetails> initializeTransaction(@RequestBody Cart cart) {
        return CompletableFuture.completedFuture(paymentService.initializePayment(cart));
    }
    @GetMapping(value = "verify", params = {"reference","payment_id"})
    @Async
    public Future<ResponseDetails> verifyTransaction(@RequestParam("reference") String reference,
                                                     @RequestParam("payment_id") String paymentId){
        return CompletableFuture.completedFuture(paymentService.verifyPayment(reference, paymentId));
    }
}
