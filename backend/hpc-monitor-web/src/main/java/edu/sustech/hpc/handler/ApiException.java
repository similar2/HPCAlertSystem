package edu.sustech.hpc.handler;

import lombok.Getter;

//refer: https://www.devglan.com/spring-security/exception-handling-in-spring-security
@Getter
public class ApiException extends RuntimeException{
    public static final ApiException UNKNOWN_ERROR=new ApiException(500,"Unknown error");

    private final int code;

    public ApiException(int code, String message){
        super(message);
        this.code=code;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(400, message);
    }
}
