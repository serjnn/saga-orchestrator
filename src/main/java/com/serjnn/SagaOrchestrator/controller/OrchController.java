package com.serjnn.SagaOrchestrator.controller;


import com.serjnn.SagaOrchestrator.dto.OrderDTO;
import com.serjnn.SagaOrchestrator.enums.SagaStepStatus;
import com.serjnn.SagaOrchestrator.services.OrchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OrchController {

    private final OrchService orchService;


    @PostMapping
    Mono<Boolean> start(@RequestBody OrderDTO orderDTO) {
        System.out.println(orderDTO);
        return orchService.start(orderDTO);

    }

    @PostMapping("/test")
    Mono<Boolean> test(@RequestBody OrderDTO orderDTO) {

        return orchService.test(orderDTO);
    }

    @GetMapping("/statuses")
    List<SagaStepStatus> statuses(){
        return orchService.getStatuses();
    }

}
