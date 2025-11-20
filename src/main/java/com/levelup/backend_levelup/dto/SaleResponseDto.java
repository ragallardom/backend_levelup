package com.levelup.backend_levelup.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SaleResponseDto {
    private Long id;
    private LocalDateTime date;
    private Long total;
    private String userEmail;
    private List<SaleItemResponseDto> items;
}