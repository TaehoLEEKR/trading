package com.trade.catalog.controller;

import com.trade.catalog.model.dto.UniverseDto;
import com.trade.catalog.model.dto.instrumentsDto;
import com.trade.catalog.service.UniversesService;
import com.trade.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/universes")
@RequiredArgsConstructor
@Slf4j
public class UniverseController {

    private final UniversesService universesService;

    @PostMapping
    public ApiResponse<?> createUniverse(
            HttpServletRequest httpServletRequest,
            @RequestBody UniverseDto.Create request
    ) {
        String token =  httpServletRequest.getHeader("Authorization").replace("Bearer ", "");

        return ApiResponse.success(universesService.createUniverse(token,request));
    }

    @PostMapping("/{universeId}/instruments")
    public ApiResponse<?> addInstrumentToUniverse(@PathVariable("universeId") String universeId, @RequestBody UniverseDto.Request instrumentId) {
        return ApiResponse.success(
                universesService.addInstrumentToUniverse(universeId, instrumentId.getInstrumentId())
        );
    }
}
