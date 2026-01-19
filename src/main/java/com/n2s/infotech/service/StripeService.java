package com.n2s.infotech.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StripeService {
    
    @Value("${stripe.secret.key}")
    private String secretKey;
    
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
        log.info("Stripe initialisé avec la clé: {}...", secretKey.substring(0, Math.min(20, secretKey.length())));
    }
    
    public Map<String, String> createPaymentIntent(Long amount) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount) // Montant en centimes (ex: 5000 = 50€)
                .setCurrency("eur")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        
        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        
        log.info("PaymentIntent créé: {}", paymentIntent.getId());
        
        return response;
    }
    
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        return PaymentIntent.retrieve(paymentIntentId);
    }
    
    public boolean verifyPaymentSuccess(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return "succeeded".equals(paymentIntent.getStatus());
        } catch (StripeException e) {
            log.error("Erreur lors de la vérification du paiement: {}", e.getMessage());
            return false;
        }
    }
}
