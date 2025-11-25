package com.levelup.backend_levelup.dto;

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
    private Integer price;
    private String imageBase64;
    private Integer stock;
    private String category;
}
