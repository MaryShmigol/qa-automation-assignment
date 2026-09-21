package com.flamingo.qa.api.common;

public record ApiResponse<T>(int statusCode, T body, String rawBody) {
}
