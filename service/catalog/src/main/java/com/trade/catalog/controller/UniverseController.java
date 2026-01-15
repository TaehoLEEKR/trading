package com.trade.catalog.controller;

import com.trade.catalog.model.dto.UniverseDto;
import com.trade.catalog.model.dto.UniverseTargetDto;
import com.trade.catalog.model.dto.instrumentsDto;
import com.trade.catalog.service.UniverseInternalService;
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
    private final UniverseInternalService universeInternalService;

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

    @GetMapping("/{universeId}/instruments")
    public ApiResponse<?> getUniverseInstruments(
            HttpServletRequest httpServletRequest,
            @PathVariable("universeId") String universeId,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "0") Integer offset
    ) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        return ApiResponse.success(universesService.getUniverseInstruments(token, universeId, size, offset));
    }



    @GetMapping("/internal/targets")
    public ApiResponse<UniverseTargetDto.UniverseTargetsResponse> getTargets(@ModelAttribute UniverseTargetDto.UniverseTargetsQuery q) {
        return ApiResponse.success(universeInternalService.getAutoIngestTargets(q.getIntervalCd(),q.getMarket(), q.getLimit()));
    }
}
