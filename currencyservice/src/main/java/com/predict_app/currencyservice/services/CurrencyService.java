package com.predict_app.currencyservice.services;

import com.predict_app.currencyservice.dtos.CurrencyRateResponseDto;

public interface CurrencyService {
    /**
     * Lấy tỉ giá chuyển đổi từ USD sang VND
     * @return CurrencyRateResponseDto chứa tỉ giá và thời gian cập nhật
     */
    CurrencyRateResponseDto getUsdToVndRate();

    /**
     * Cập nhật tỉ giá từ external API và lưu vào cache
     */
    void updateExchangeRate();
}

