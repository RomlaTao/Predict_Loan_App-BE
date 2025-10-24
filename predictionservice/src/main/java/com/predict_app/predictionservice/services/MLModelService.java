package com.predict_app.predictionservice.services;

import com.predict_app.predictionservice.dtos.MLModelRequestDto;
import com.predict_app.predictionservice.dtos.MLModelResponseDto;

public interface MLModelService {
    MLModelResponseDto predict(MLModelRequestDto request);
}
