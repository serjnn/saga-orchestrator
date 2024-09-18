package com.serjnn.SagaOrchestrator.steps;

import com.serjnn.SagaOrchestrator.dto.OrderDTO;
import reactor.core.publisher.Mono;

public interface SagaStep {

    Mono<Boolean> process (OrderDTO orderDTO);

    Mono<Boolean> revert(OrderDTO orderDTO);

}
