package com.predict_app.predictionservice.services;

import com.predict_app.predictionservice.dtos.MLModelRequestDto;
import com.predict_app.predictionservice.dtos.MLModelResponseDto;

public interface RabbitMQProducerService {

    MLModelResponseDto sendPredictionRequest(MLModelRequestDto request);
    MLModelResponseDto waitForResponse(String correlationId);
}
