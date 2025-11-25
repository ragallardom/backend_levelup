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
import org.springframework.data.domain.Sort;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Optional<Product> existingProduct = productRepository.findByCode(requestDto.getCode());
        if (existingProduct.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "el producto con este codigo ya existe: " + requestDto.getCode());
        }

        Product product = Product.builder()
                .code(requestDto.getCode())
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .stock(requestDto.getStock())
                .category(requestDto.getCategory())
                .build();

        if (requestDto.getImageBase64() != null && !requestDto.getImageBase64().isEmpty()) {
            byte[] imageBytes = java.util.Base64.getDecoder().decode(requestDto.getImageBase64());
            product.setImageData(imageBytes);
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        String base64Image = null;
        if (product.getImageData() != null) {
            base64Image = java.util.Base64.getEncoder().encodeToString(product.getImageData());
        }
        return ProductResponseDto.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .imageBase64(base64Image)
                .build();


    }
}
