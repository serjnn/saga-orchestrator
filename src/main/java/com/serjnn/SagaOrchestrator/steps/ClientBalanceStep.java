package com.serjnn.SagaOrchestrator.steps;

import com.serjnn.SagaOrchestrator.dto.OrderDTO;
import com.serjnn.SagaOrchestrator.enums.SagaStepStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Component
public class ClientBalanceStep implements SagaStep {
    private SagaStepStatus status;

    public ClientBalanceStep(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    private final WebClient webClient;


    @Override
    public Mono<Boolean> process(OrderDTO orderDTO) {
        System.out.println("client process");

        return webClient.post()
                .uri("lb://client/api/v1/deduct")
                .body(BodyInserters.fromValue(orderDTO))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        setStatus(SagaStepStatus.COMPLETE);
                        System.out.println("Client " + getStatus());
                        return Mono.just(true);
                    } else {
                        setStatus(SagaStepStatus.FAILED);
                        System.out.println("client failed with status: " + response.statusCode());
                        return Mono.just(false);
                    }
                })
                .onErrorResume(e -> {
                    System.out.println("Client service is unavailable, triggering rollback.");
                    setStatus(SagaStepStatus.FAILED);
                    return Mono.error(new RuntimeException("Client service is unavailable"));
                });
    }




    @Override
    public Mono<Boolean> revert(OrderDTO orderDTO) {
        System.out.println("client revert");

        return webClient.post()
                    .uri("lb://client/api/v1/restore")
                    .body(BodyInserters.fromValue(orderDTO))
                    .exchangeToMono(response -> {
            if (response.statusCode().is2xxSuccessful()) {
                setStatus(SagaStepStatus.COMPLETE);

                return Mono.just(true);
            } else {
                setStatus(SagaStepStatus.FAILED);

                return Mono.just(false);
            }
        })
                .onErrorResume(e -> {
                    System.out.println("Client service is unavailable, triggering rollback.");
                    setStatus(SagaStepStatus.FAILED);
                    return Mono.error(new RuntimeException("Client service is unavailable"));
                });
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
