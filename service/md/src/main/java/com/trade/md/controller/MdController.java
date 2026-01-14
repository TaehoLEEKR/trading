package com.trade.md.controller;

import com.trade.common.response.ApiResponse;
import com.trade.md.model.dto.IngestDto;
import com.trade.md.model.dto.Md;
import com.trade.md.service.MdService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/md")
@RequiredArgsConstructor
@Slf4j
public class MdController {

    private final MdService mdService;

    @PostMapping("/ingest/bars")
    public ApiResponse<Md.ResponseBars> ingestBars(
            HttpServletRequest httpServletRequest,
            @RequestBody @Valid Md.RequestBars request) {
        String token = httpServletRequest.getHeader("Authorization").replace("Bearer ", "");
        return ApiResponse.success(mdService.ingestDailyBars(request,token));
    }

    @GetMapping("/bars")
    public ApiResponse<IngestDto.BarsResponse> getBars(
            @RequestParam String instrumentId,
            @RequestParam(defaultValue = "1d") String intervalCd,
            @RequestParam String from,   // yyyy-MM-dd
            @RequestParam String to,     // yyyy-MM-dd
            @RequestParam(defaultValue = "200") int size,
            @RequestParam(defaultValue = "ASC") String order
    ) {
        return ApiResponse.success(mdService.getBars(instrumentId, intervalCd, from, to, size, order));
    }
}
