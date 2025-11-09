package com.levelup.backend_levelup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.levelup.backend_levelup.dto.ProductRequestDto;
import com.levelup.backend_levelup.dto.ProductResponseDto;
import com.levelup.backend_levelup.model.Product;
import com.levelup.backend_levelup.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProductsReturnsMappedResponse() {
        Product product = Product.builder()
                .id(1L)
                .code("PRD-001")
                .name("Test Product")
                .description("Description")
                .price(BigDecimal.valueOf(9.99))
                .imagePath("/images/test.png")
                .stock(10)
                .build();
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponseDto> products = productService.getAllProducts();

        assertThat(products).hasSize(1);
        ProductResponseDto responseDto = products.getFirst();
        assertThat(responseDto.getId()).isEqualTo(product.getId());
        assertThat(responseDto.getCode()).isEqualTo(product.getCode());
        assertThat(responseDto.getName()).isEqualTo(product.getName());
        assertThat(responseDto.getDescription()).isEqualTo(product.getDescription());
        assertThat(responseDto.getPrice()).isEqualByComparingTo(product.getPrice());
        assertThat(responseDto.getImagePath()).isEqualTo(product.getImagePath());
        assertThat(responseDto.getStock()).isEqualTo(product.getStock());
    }

    @Test
    void createProductReturnsResponseWhenCodeIsUnique() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .code("PRD-002")
                .name("Another Product")
                .description("Another Description")
                .price(BigDecimal.valueOf(19.99))
                .imagePath("/images/another.png")
                .stock(5)
                .build();

        Product savedProduct = Product.builder()
                .id(2L)
                .code(requestDto.getCode())
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .imagePath(requestDto.getImagePath())
                .stock(requestDto.getStock())
                .build();

        when(productRepository.findByCode(requestDto.getCode())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponseDto responseDto = productService.createProduct(requestDto);

        assertThat(responseDto.getId()).isEqualTo(savedProduct.getId());
        assertThat(responseDto.getCode()).isEqualTo(savedProduct.getCode());
        assertThat(responseDto.getName()).isEqualTo(savedProduct.getName());
        assertThat(responseDto.getDescription()).isEqualTo(savedProduct.getDescription());
        assertThat(responseDto.getPrice()).isEqualByComparingTo(savedProduct.getPrice());
        assertThat(responseDto.getImagePath()).isEqualTo(savedProduct.getImagePath());
        assertThat(responseDto.getStock()).isEqualTo(savedProduct.getStock());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProductThrowsConflictWhenCodeAlreadyExists() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .code("PRD-003")
                .name("Existing Product")
                .description("Existing Description")
                .price(BigDecimal.valueOf(29.99))
                .imagePath("/images/existing.png")
                .stock(8)
                .build();

        Product existingProduct = Product.builder()
                .id(3L)
                .code(requestDto.getCode())
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .imagePath(requestDto.getImagePath())
                .stock(requestDto.getStock())
                .build();

        when(productRepository.findByCode(requestDto.getCode())).thenReturn(Optional.of(existingProduct));

        assertThatThrownBy(() -> productService.createProduct(requestDto))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(exception -> ((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(productRepository, times(0)).save(any(Product.class));
    }
}
