package com.trade.catalog.controller;

import com.trade.catalog.model.dto.instrumentsDto;
import com.trade.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/catalog")
@Slf4j
@RequiredArgsConstructor
public class CatalogController {

    @PostMapping("/instruments")
    public ApiResponse<?> getInstruments(@RequestBody instrumentsDto.Reqeust reqeust) {
        return null;
    }
}
