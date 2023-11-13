package com.gallery.backend.controller;

import com.gallery.backend.dto.OrderDto;
import com.gallery.backend.entity.Cart;
import com.gallery.backend.entity.Item;
import com.gallery.backend.entity.Order;
import com.gallery.backend.repository.CartRepository;
import com.gallery.backend.repository.OrderRepository;
import com.gallery.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderController {

    private final JwtService jwtService;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @GetMapping("/api/orders")
    public ResponseEntity getOrder(
            @CookieValue(value = "token", required = false) String token) {

        if (!jwtService.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        int memberId = jwtService.getId(token);

        List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(memberId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/api/orders")
    public ResponseEntity pushOrder(
            @RequestBody OrderDto orderDto,
            @CookieValue(value = "token", required = false) String token){

        if (!jwtService.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        int memberId = jwtService.getId(token);
        Order order = new Order();

        order.setMemberId(jwtService.getId(token));
        order.setName(orderDto.getName());
        order.setAddress(orderDto.getAddress());
        order.setPayment(orderDto.getPayment());
        order.setCardNumber(orderDto.getCardNumber());
        order.setItems(orderDto.getItems());

        orderRepository.save(order);
        cartRepository.deleteByMemberId(memberId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
