package com.levelup.backend_levelup.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private String imagePath;
    private Integer stock;
}
