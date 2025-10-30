package com.predict_app.predictionservice.publishers;

import com.predict_app.predictionservice.dtos.events.PredictionRequestedEventDto;
import com.predict_app.predictionservice.dtos.events.ModelPredictRequestedEventDto;
import com.predict_app.predictionservice.dtos.events.PredictionCompletedEventDto;

public interface PredictionEventPublisher {
    void publishCustomerProfileRequestedEvent(PredictionRequestedEventDto predictionRequestedEventDto);
    void publishModelPredictRequestedEvent(ModelPredictRequestedEventDto modelPredictRequestedEventDto);
    void publishPredictionCompletedEvent(PredictionCompletedEventDto predictionCompletedEventDto);
}
