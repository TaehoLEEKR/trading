package com.trade.catalog.controller;

import com.trade.catalog.model.dto.instrumentsDto;
import com.trade.catalog.service.InstrumentService;
import com.trade.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/catalog")
@Slf4j
@RequiredArgsConstructor
public class CatalogController {

    private final InstrumentService instrumentService;

    @GetMapping("/instruments")
    public ApiResponse<?> getInstruments(@ModelAttribute instrumentsDto.Request req) {
        return ApiResponse.success(instrumentService.getInstruments(req));
    }

    @GetMapping("/instruments/{instrumentId}")
    public ApiResponse<?> getInstrument(@PathVariable String instrumentId) {
        return ApiResponse.success(instrumentService.getInstrumentByOne(instrumentId));
    }
}
