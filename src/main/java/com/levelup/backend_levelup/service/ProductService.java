package com.levelup.backend_levelup.service;

import com.levelup.backend_levelup.dto.ProductRequestDto;
import com.levelup.backend_levelup.dto.ProductResponseDto;
import com.levelup.backend_levelup.model.Product;
import com.levelup.backend_levelup.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Optional<Product> existingProduct = productRepository.findByCode(requestDto.getCode());
        if (existingProduct.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A product with the provided code already exists: " + requestDto.getCode());
        }

        Product product = Product.builder()
                .code(requestDto.getCode())
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .stock(requestDto.getStock())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
}
