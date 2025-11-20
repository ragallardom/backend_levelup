package com.levelup.backend_levelup.controller;

import com.levelup.backend_levelup.dto.SaleRequestDto;
import com.levelup.backend_levelup.dto.SaleResponseDto;
import com.levelup.backend_levelup.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<SaleResponseDto> createSale(@RequestBody SaleRequestDto request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return ResponseEntity.ok(saleService.createSale(request, username));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponseDto>> getSales() {



         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String username = authentication.getName();

        return ResponseEntity.ok(saleService.getSalesByUser(username));
    }
}