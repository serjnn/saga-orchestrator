package com.serjnn.SagaOrchestrator.services;


import com.serjnn.SagaOrchestrator.dto.OrderDTO;
import com.serjnn.SagaOrchestrator.enums.SagaStepStatus;
import com.serjnn.SagaOrchestrator.steps.BucketStep;
import com.serjnn.SagaOrchestrator.steps.ClientBalanceStep;
import com.serjnn.SagaOrchestrator.steps.OrderStep;
import com.serjnn.SagaOrchestrator.steps.SagaStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrchService {


    private final ClientBalanceStep clientBalanceStep;
    private final BucketStep bucketStep;
    private final OrderStep orderStep;

    private List<SagaStep> getSteps() {
        return List.of(clientBalanceStep, bucketStep, orderStep);
    }


    public Mono<Boolean> start(OrderDTO orderDTO) {
        System.out.println("starting transaction");

        return Flux.fromIterable(getSteps())
                .concatMap(step -> step.process(orderDTO)
                        .flatMap(success -> {
                            if (success) {
                                step.setStatus(SagaStepStatus.COMPLETE);
                                return Mono.just(true);
                            } else {
                                step.setStatus(SagaStepStatus.FAILED);

                                return this.revert(orderDTO).then(Mono.just(false));
                            }
                        })
                        .onErrorResume(e -> {
                            System.out.println("Error in process: " + e.getMessage() + ", triggering rollback.");
                            step.setStatus(SagaStepStatus.FAILED);

                            return this.revert(orderDTO).then(Mono.just(false));
                        }))
                .takeWhile(success -> success)
                .then(Mono.just(true))
                .doFinally(this::resetSteps);
    }


    private void resetSteps(SignalType signalType) {
        getSteps().forEach(SagaStep::resetStep);
    }

    private Mono<Void> revert(OrderDTO orderDTO) {
        return Flux.fromIterable(getSteps())
                .filter(step -> step.getStatus() == SagaStepStatus.COMPLETE)
                .concatMap(step -> step.revert(orderDTO))
                .then();
    }


}