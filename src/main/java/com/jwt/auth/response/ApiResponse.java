package com.jwt.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean status;
    private String message;
    private T data;

    public ApiResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }
}
