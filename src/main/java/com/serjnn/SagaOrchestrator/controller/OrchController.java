package com.serjnn.SagaOrchestrator.controller;


import com.serjnn.SagaOrchestrator.dto.OrderDTO;
import com.serjnn.SagaOrchestrator.services.OrchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrchController {

    private final OrchService orchService;


    @PostMapping
    void start(@RequestBody OrderDTO orderDTO){

        orchService.start(orderDTO);

    }
}
