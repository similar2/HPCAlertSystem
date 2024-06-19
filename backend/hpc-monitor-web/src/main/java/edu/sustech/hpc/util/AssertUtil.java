package edu.sustech.hpc.util;

import edu.sustech.hpc.handler.ApiException;

public class AssertUtil {
    public static void asserts(boolean condition, String message){
        if(!condition){
            throw ApiException.badRequest(message);
        }
    }
}
