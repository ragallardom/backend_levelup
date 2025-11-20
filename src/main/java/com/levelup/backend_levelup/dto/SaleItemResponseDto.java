package com.levelup.backend_levelup.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaleItemResponseDto {
    private String productName;
    private Integer quantity;
    private Integer price;
    private Integer subTotal;
}