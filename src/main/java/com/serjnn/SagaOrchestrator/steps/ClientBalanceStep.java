package com.serjnn.SagaOrchestrator.steps;

import com.serjnn.SagaOrchestrator.dto.OrderDTO;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ClientBalanceStep implements SagaStep {
    public ClientBalanceStep(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    private final WebClient webClient;


    @Override
    public Mono<Boolean> process(OrderDTO orderDTO) {
        return webClient.post()
                .uri("lb://client/api/v1/deduct")
                .body(BodyInserters.fromValue(orderDTO.getClientID()))
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }


    @Override
    public Mono<Boolean> revert(OrderDTO orderDTO) {
        {
            return webClient.post()
                    .uri("lb://client/api/v1/restore")
                    .body(BodyInserters.fromValue(orderDTO.getClientID()))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorReturn(false);
        }
    }
}
