package com.levelup.backend_levelup.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sale_items")
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sale_items_seq")
    @SequenceGenerator(name = "sale_items_seq", sequenceName = "SALE_ITEMS_SEQ", allocationSize = 1)
    private Long id;

    private Integer quantity;

    private Integer priceAtPurchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}