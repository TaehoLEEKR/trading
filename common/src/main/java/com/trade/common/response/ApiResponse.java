package com.trade.common.response;

import com.trade.common.constant.ErrorCode;
import com.trade.common.constant.ResponseCode;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class ApiResponse<T> {
    private Status status;
    private T data;

    @Getter
    @Builder
    public static class Status {
        private int code;
        private String message;
    }


    public static <T> ApiResponse<T> success(T data) { // 기본 디폴트값
        return success(ResponseCode.SUCCESS , data);
    }

    // 실패 응답 생성
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return error(errorCode, null);
    }

    public static <T> ApiResponse<T> errorResponse(ErrorCode errorCode, T data) {
        return error(errorCode, data);
    }

    // 성공 응답 생성
    public static <T> ApiResponse<T> success(ResponseCode status, T data) {
        return ApiResponse.<T>builder()
                .status(Status.builder()
                        .code(status.getCode())
                        .message(status.getMessage())
                        .build())
                .data(data)
                .build();
    }

    // 확장된 실패 응답 생성
    public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
        return ApiResponse.<T>builder()
                .status(Status.builder()
                        .code(errorCode.getErrorCode())
                        .message(errorCode.getMessage())
                        .build())
                .data(data)
                .build();
    }


}
