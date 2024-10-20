package com.serjnn.SagaOrchestrator.steps;

import com.serjnn.SagaOrchestrator.dto.OrderDTO;
import com.serjnn.SagaOrchestrator.enums.SagaStepStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class OrderStep implements SagaStep {
    private SagaStepStatus status = SagaStepStatus.PENDING;

    public OrderStep(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    private final WebClient webClient;


    @Override
    public Mono<Boolean> process(OrderDTO orderDTO) {
        log.info("order process");

        return webClient.post()
                .uri("lb://order/api/v1/create")
                .body(BodyInserters.fromValue(orderDTO))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        setStatus(SagaStepStatus.COMPLETE);
                        return Mono.just(true);
                    } else {
                        setStatus(SagaStepStatus.FAILED);
                        log.info("order failed with status: " + response.statusCode());
                        return Mono.just(false);
                    }
                })
                .onErrorResume(e -> {
                    log.info("order service is unavailable, triggering rollback.");
                    setStatus(SagaStepStatus.FAILED);
                    return Mono.error(new RuntimeException("order service is unavailable"));
                });
    }





    @Override
    public Mono<Boolean> revert(OrderDTO orderDTO) {
        log.info("order revert");

        return webClient.post()
                    .uri("lb://order/api/v1/remove")
                    .body(BodyInserters.fromValue(orderDTO.getOrderId()))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .onErrorReturn(false);

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

