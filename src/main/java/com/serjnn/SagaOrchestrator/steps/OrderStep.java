package com.serjnn.SagaOrchestrator.steps;

import com.serjnn.SagaOrchestrator.dto.OrderDTO;
import com.serjnn.SagaOrchestrator.enums.SagaStepStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class OrderStep implements SagaStep {
    private SagaStepStatus status = SagaStepStatus.PENDING;

    public OrderStep(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    private final WebClient webClient;


    @Override
    public Mono<Boolean> process(OrderDTO orderDTO) {
        System.out.println("order process");

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
                    .body(BodyInserters.fromValue(orderDTO.getOrderId()))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorReturn(false);
        }
    }
    @Override
    public SagaStepStatus getStatus() {
        return status;
    }
    @Override
    public void setStatus(SagaStepStatus status) {
        this.status = status;
    }
}

