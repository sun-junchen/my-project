package com.example.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public record RestBean<T>(int code, T data, String message) {
    static Gson gson = new GsonBuilder().serializeNulls().create();

    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200, data, "请求成功");
    }

    public static <T> RestBean<T> success() {
        return success(null);
    }

    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, null, message);
    }


    public String asJsonString() {
        return gson.toJson(this);
    }
}
