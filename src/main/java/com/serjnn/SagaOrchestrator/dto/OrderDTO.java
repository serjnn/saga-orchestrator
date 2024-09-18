package com.serjnn.SagaOrchestrator.dto;


import lombok.Getter;

@Getter
public class OrderDTO {

    private Long clientID;

    private Long id;

    private int quantity;

    private String name;

    private int price;

}
