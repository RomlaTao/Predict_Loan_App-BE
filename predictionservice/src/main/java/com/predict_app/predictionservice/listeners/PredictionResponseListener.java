package com.predict_app.predictionservice.listeners;

import com.predict_app.predictionservice.dtos.PredictionMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PredictionResponseListener {

    @RabbitListener(queues = "${rabbitmq.queue.response}")
    public void handlePredictionResponse(PredictionMessageDto message) {
        log.info("Received prediction response for correlationId: {}", 
                message.getCorrelationId());
        
        try {
            // Process response from ML Model
            if ("COMPLETED".equals(message.getStatus())) {
                log.info("Prediction completed successfully");
                // Có thể thêm logic xử lý kết quả thành công ở đây
                // Ví dụ: gửi notification, cập nhật cache, etc.
                
            } else if ("FAILED".equals(message.getStatus())) {
                log.error("Prediction failed: {}", message.getResponse().getMessage());
                // Có thể thêm logic xử lý lỗi ở đây
                // Ví dụ: gửi alert, retry mechanism, etc.
            }
            
            log.info("Prediction result: {}", message.getResponse());
            log.info("Status: {}", message.getStatus());
            
        } catch (Exception e) {
            log.error("Error processing prediction response: {}", e.getMessage(), e);
        }
    }
}
