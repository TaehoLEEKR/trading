package com.trade.md.controller;

import com.trade.common.response.ApiResponse;
import com.trade.md.model.dto.Md;
import com.trade.md.service.MdService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ApiResponse.success(mdService.KISDataParsingAndSavingToJob(request,token));
    }
}
