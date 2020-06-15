package io.github.jzdayz.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class R {

    private static final int SUCCESS_CODE = 1000;

    private static final int ERROR_CODE = 2000;

    @Builder.Default
    private int code = SUCCESS_CODE;
    @Builder.Default
    private String message = "success";
    private Map<String,Object> body = new HashMap<>();

    public static R ok(){
        return R.builder().build();
    }

    public static R error(){
        return R.builder().code(ERROR_CODE).build();
    }

    public static R error(String message){
        return R.builder().code(ERROR_CODE).message(message).build();
    }

    public static R ok(Map<String,Object> body){
        return R.builder().body(body).build();
    }
}
