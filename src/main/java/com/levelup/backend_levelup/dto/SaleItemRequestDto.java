package com.levelup.backend_levelup.dto;

import lombok.Data;

@Data
public class SaleItemRequestDto {
    private Long productId;
    private Integer quantity;
}