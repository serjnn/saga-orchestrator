package com.serjnn.SagaOrchestrator.steps;

import com.serjnn.SagaOrchestrator.dto.OrderDTO;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


public class OrderStep implements SagaStep {
    public OrderStep(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    private final WebClient webClient;


    @Override
    public Mono<Boolean> process(OrderDTO orderDTO) {
        return webClient.post()
                .uri("lb://order/api/v1/create")
                .body(BodyInserters.fromValue(orderDTO))
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }


    @Override
    public Mono<Boolean> revert(OrderDTO orderDTO) {
        {
            return webClient.post()
                    .uri("lb://order/api/v1/remove")
                    .body(BodyInserters.fromValue(orderDTO))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorReturn(false);
        }
    }
}

