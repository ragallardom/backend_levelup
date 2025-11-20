package com.levelup.backend_levelup.service;

import com.levelup.backend_levelup.dto.*;
import com.levelup.backend_levelup.model.*;
import com.levelup.backend_levelup.repository.ProductRepository;
import com.levelup.backend_levelup.repository.SaleRepository;
import com.levelup.backend_levelup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public SaleResponseDto createSale(SaleRequestDto request, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Sale sale = Sale.builder()
                .date(LocalDateTime.now())
                .user(user)
                .items(new ArrayList<>())
                .build();

        long totalAmount = 0;


        for (SaleItemRequestDto itemRequest : request.getItems()) {


            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado ID: " + itemRequest.getProductId()));


            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
            }
            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);


            SaleItem saleItem = SaleItem.builder()
                    .product(product)
                    .sale(sale)
                    .quantity(itemRequest.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();


            totalAmount += (long) product.getPrice() * itemRequest.getQuantity();


            sale.getItems().add(saleItem);
        }

        sale.setTotalAmount(totalAmount);


        Sale savedSale = saleRepository.save(sale);


        return mapToResponseDto(savedSale);
    }

    private SaleResponseDto mapToResponseDto(Sale sale) {
        List<SaleItemResponseDto> itemDtos = sale.getItems().stream()
                .map(item -> SaleItemResponseDto.builder()
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPriceAtPurchase())
                        .subTotal(item.getPriceAtPurchase() * item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return SaleResponseDto.builder()
                .id(sale.getId())
                .date(sale.getDate())
                .total(sale.getTotalAmount())
                .userEmail(sale.getUser().getEmail())
                .items(itemDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public List<SaleResponseDto> getSalesByUser(String username) {


        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        List<Sale> sales = saleRepository.findByUserId(user.getId());

        return sales.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
}