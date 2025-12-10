package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CreateOrderRequest;
import com.n2s.infotech.dto.OrderDto;
import com.n2s.infotech.dto.OrderItemRequestDto;
import com.n2s.infotech.model.*;
import com.n2s.infotech.repository.CartItemRepository;
import com.n2s.infotech.repository.ListingRepository;
import com.n2s.infotech.repository.OrderRepository;
import com.n2s.infotech.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    @PostMapping
    @Transactional
    public OrderDto create(@Valid @RequestBody CreateOrderRequest req) {
        User buyer = userRepository.findById(req.getBuyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Order order = Order.builder().buyer(buyer).status("CREATED").build();
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDto it : req.getItems()) {
            Listing listing = listingRepository.findById(it.getListingId())
                    .orElseThrow(() -> new RuntimeException("Listing not found"));

            // Check stock availability
            if (!listing.getActive() || listing.getQuantity() < it.getQuantity()) {
                throw new RuntimeException("Insufficient stock for listing: " + listing.getId());
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .listing(listing)
                    .quantity(it.getQuantity())
                    .price(listing.getPrice())
                    .build();
            items.add(orderItem);

            total = total.add(listing.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())));

            // Reduce quantity
            listing.setQuantity(listing.getQuantity() - it.getQuantity());
            if (listing.getQuantity() == 0) {
                listing.setActive(false);
            }
            listingRepository.save(listing);
        }

        order.setItems(items);
        order.setTotal(total);
        order = orderRepository.save(order);

        // Clear cart after successful order
        cartItemRepository.deleteByUserId(buyer.getId());

        return toDto(order);
    }

    @GetMapping
    public List<OrderDto> getUserOrders(@RequestParam Long userId) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getBuyer().getId().equals(userId))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public OrderDto getOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return toDto(order);
    }


    private OrderDto toDto(Order order) {
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
