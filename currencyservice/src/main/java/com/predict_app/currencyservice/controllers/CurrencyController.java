package com.predict_app.currencyservice.controllers;

import com.predict_app.currencyservice.dtos.CurrencyRateResponseDto;
import com.predict_app.currencyservice.services.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/rate/USD/VND")
    public ResponseEntity<CurrencyRateResponseDto> getUsdToVndRate() {
        log.info("[CURRENCY] Request received for USD to VND rate");
        CurrencyRateResponseDto rate = currencyService.getUsdToVndRate();
        return ResponseEntity.ok(rate);
    }
}

