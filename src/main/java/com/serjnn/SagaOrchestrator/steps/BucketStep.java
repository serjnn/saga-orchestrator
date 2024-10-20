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
public class BucketStep implements SagaStep {


    private SagaStepStatus status;

    public BucketStep(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }


    private final WebClient webClient;


    @Override
    public Mono<Boolean> process(OrderDTO orderDTO) {
        log.info("bucket process");

        return webClient.post()
                .uri("lb://bucket/api/v1/clear")
                .body(BodyInserters.fromValue(orderDTO.getClientID()))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        setStatus(SagaStepStatus.COMPLETE);

                        return Mono.just(true);
                    } else {
                        setStatus(SagaStepStatus.FAILED);
                        log.info("Bucket failed with status: " + response.statusCode());

                        return Mono.just(false);
                    }
                })
                .onErrorResume(e -> {
                    log.info("Bucket service is unavailable, triggering rollback.");
                    setStatus(SagaStepStatus.FAILED);
                    return Mono.error(new RuntimeException("Bucket service is unavailable"));
                });
    }




    @Override
    public Mono<Boolean> revert(OrderDTO orderDTO) {
        log.info("bucket revert");

        return webClient.post()
                    .uri("lb://bucket/api/v1/restore")
                    .body(BodyInserters.fromValue(orderDTO))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                .onErrorResume(e -> {
                    log.info("Bucket service is unavailable, triggering rollback.");
                    return Mono.error(new RuntimeException("Bucket service is unavailable"));
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
