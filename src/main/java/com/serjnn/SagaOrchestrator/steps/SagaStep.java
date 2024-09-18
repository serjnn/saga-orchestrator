package com.serjnn.SagaOrchestrator.steps;

import com.serjnn.SagaOrchestrator.dto.OrderDTO;
import com.serjnn.SagaOrchestrator.enums.SagaStepStatus;
import reactor.core.publisher.Mono;

public interface SagaStep {

    Mono<Boolean> process (OrderDTO orderDTO);

    Mono<Boolean> revert(OrderDTO orderDTO);

    SagaStepStatus getStatus();
    void setStatus(SagaStepStatus status);

     default void resetStep(){
setStatus(SagaStepStatus.PENDING);
     }

}
