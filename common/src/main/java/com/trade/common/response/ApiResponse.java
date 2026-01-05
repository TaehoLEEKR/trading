package com.trade.common.response;
import java.time.Instant;

public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ApiError error;
    private final ApiMeta meta;

    private ApiResponse(boolean success, T data, ApiError error, ApiMeta meta) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.meta = meta;
    }

    public static <T> ApiResponse<T> ok(T data, ApiMeta meta) {
        return new ApiResponse<>(true, data, null, meta);
    }

    public static ApiResponse<Void> ok(ApiMeta meta) {
        return new ApiResponse<>(true, null, null, meta);
    }

    public static <T> ApiResponse<T> fail(ApiError error, ApiMeta meta) {
        return new ApiResponse<>(false, null, error, meta);
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public ApiError getError() { return error; }
    public ApiMeta getMeta() { return meta; }

    public static ApiMeta meta(String requestId, String runId) {
        return new ApiMeta(requestId, runId, Instant.now());
    }
}
