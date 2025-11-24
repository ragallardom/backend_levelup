package com.levelup.backend_levelup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotBlank
    @Size(max = 64)
    private String code;

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;

    @NotNull
    @PositiveOrZero
    private Integer price;


    @NotNull
    @PositiveOrZero
    private Integer stock;
}
