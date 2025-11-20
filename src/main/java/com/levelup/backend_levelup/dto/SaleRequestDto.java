package com.levelup.backend_levelup.dto;

import lombok.Data;
import java.util.List;

@Data
public class SaleRequestDto {
    private List<SaleItemRequestDto> items;
}