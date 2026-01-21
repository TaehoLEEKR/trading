package com.trade.md.controller;


import com.trade.common.response.ApiResponse;
import com.trade.md.model.dto.MdIngest;
import com.trade.md.service.MdBulkIngestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/md")
@RequiredArgsConstructor
public class MdIngestController {

    private final MdBulkIngestService mdBulkIngestService;

    @PostMapping("universe/ingest/bars")
    public ApiResponse<MdIngest.UniverseBarsResponse> ingestUniverseBars(
            HttpServletRequest req,
            @RequestBody @Valid MdIngest.UniverseBarsRequest request
    ) {
//        String token = req.getHeader("Authorization").replace("Bearer ", "");
        return ApiResponse.success(mdBulkIngestService.ingestDailyBarsByUniverse(request));
    }
}