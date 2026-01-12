package com.trade.md.controller;

import com.trade.common.response.ApiResponse;
import com.trade.md.model.dto.Md;
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

    @PostMapping("/ingest/bars")
    public ApiResponse<Md.ResponseBars> ingestBars(@RequestBody @Valid Md.RequestBars request) {
        return ApiResponse.success(null);
    }
}
