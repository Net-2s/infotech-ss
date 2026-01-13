package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CreateOrderRequest;
import com.n2s.infotech.dto.OrderDto;
import com.n2s.infotech.model.Order;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.OrderRepository;
import com.n2s.infotech.repository.UserRepository;
import com.n2s.infotech.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
@Tag(name = "User Orders", description = "Gestion des commandes de l'utilisateur connecte")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class UserOrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Recuperer mes commandes", description = "Liste toutes les commandes de l'utilisateur connecte")
    public ResponseEntity<List<OrderDto>> getMyOrders(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderDto> orders = orderService.getUserOrders(user.getId());

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer une commande", description = "Recupere les details d'une commande specifique")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getBuyer().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    @Operation(summary = "Creer une commande", description = "Cree une nouvelle commande a partir du panier")
    public ResponseEntity<OrderDto> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        request.setBuyerId(user.getId());

        OrderDto orderDto = orderService.createOrder(request);

        // L'email de confirmation est envoye par le frontend Angular
        // via POST /api/emails/send apres generation du HTML
        log.info("Commande #{} creee pour l'utilisateur {}", orderDto.getId(), user.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler une commande", description = "Annule une commande si elle n'est pas encore expediee")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long id, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getBuyer().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if ("SHIPPED".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order in current status: " + order.getStatus());
        }

        OrderDto cancelledOrder = orderService.updateOrderStatus(id, "CANCELLED");

        return ResponseEntity.ok(cancelledOrder);
    }
}

