package com.n2s.infotech.service;

import com.n2s.infotech.dto.CreateOrderRequest;
import com.n2s.infotech.dto.OrderDto;
import com.n2s.infotech.dto.OrderItemRequestDto;
import com.n2s.infotech.model.Listing;
import com.n2s.infotech.model.Order;
import com.n2s.infotech.model.OrderItem;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.CartItemRepository;
import com.n2s.infotech.repository.ListingRepository;
import com.n2s.infotech.repository.OrderRepository;
import com.n2s.infotech.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour gérer les commandes
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Crée une nouvelle commande
     */
    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Order order = Order.builder()
                .buyer(buyer)
                .status("CREATED")
                .build();

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDto itemRequest : request.getItems()) {
            Listing listing = listingRepository.findById(itemRequest.getListingId())
                    .orElseThrow(() -> new RuntimeException("Listing not found: " + itemRequest.getListingId()));

            // Vérifier la disponibilité
            if (!listing.getActive() || listing.getQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient stock for listing: " + listing.getId());
            }

            // Créer l'item de commande
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .listing(listing)
                    .quantity(itemRequest.getQuantity())
                    .price(listing.getPrice())
                    .build();
            items.add(orderItem);

            // Calculer le total
            total = total.add(listing.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

            // Réduire le stock
            listing.setQuantity(listing.getQuantity() - itemRequest.getQuantity());
            if (listing.getQuantity() == 0) {
                listing.setActive(false);
            }
            listingRepository.save(listing);
        }

        order.setItems(items);
        order.setTotal(total);
        order = orderRepository.save(order);

        // Vider le panier après la commande
        cartItemRepository.deleteByUserId(buyer.getId());

        return convertToDto(order);
    }

    /**
     * Récupère toutes les commandes d'un utilisateur
     */
    public List<OrderDto> getUserOrders(Long userId) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getBuyer().getId().equals(userId))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une commande par son ID
     */
    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDto(order);
    }

    /**
     * Met à jour le statut d'une commande
     */
    public OrderDto updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        order = orderRepository.save(order);

        return convertToDto(order);
    }

    /**
     * Convertit une Order en DTO
     */
    private OrderDto convertToDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .buyerId(order.getBuyer().getId())
                .createdAt(order.getCreatedAt())
                .total(order.getTotal())
                .status(order.getStatus())
                .items(order.getItems().stream().map(item ->
                        OrderDto.OrderItemDto.builder()
                                .id(item.getId())
                                .listingId(item.getListing().getId())
                                .productTitle(item.getListing().getProduct().getTitle())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }
}

