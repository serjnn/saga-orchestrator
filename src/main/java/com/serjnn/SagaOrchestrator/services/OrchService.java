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
        return List.of(bucketStep, clientBalanceStep, orderStep);
    }


    public Mono<Void> start(OrderDTO orderDTO) {
        System.out.println("я сказала стартуем");
        return Flux.fromIterable(getSteps())
                .concatMap(step -> step.process(orderDTO)
                        .flatMap(success -> {
                            if (!success) {

                                return this.revert(orderDTO);
                            }
                            return Mono.just(success);
                        }))
                .then();


    }



    private void resetSteps(SignalType signalType) {
        getSteps().forEach(SagaStep::resetStep);
    }

    private Mono<Void> revert(OrderDTO orderDTO) {
        return Flux.fromIterable(getSteps())
                .filter(step -> step.getStatus() == SagaStepStatus.COMPLETE)
                .flatMap(step -> step.revert(orderDTO))
                .then();
    }


    public Mono<Boolean> test(OrderDTO orderDTO) {
       return clientBalanceStep.process(orderDTO);

    }
}