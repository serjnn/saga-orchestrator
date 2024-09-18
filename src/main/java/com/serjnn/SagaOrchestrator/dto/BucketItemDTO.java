package com.serjnn.SagaOrchestrator.dto;


import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BucketItemDTO {

    private Long id;
    private String name;
    private Integer quantity;
    private BigDecimal price;

    @Override
    public String toString() {
        return "BucketItemDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
