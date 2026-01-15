package com.trade.run.model;


public record ApiResponse<T>(Status status, T data) {
    public record Status(int code, String message) {}
}
