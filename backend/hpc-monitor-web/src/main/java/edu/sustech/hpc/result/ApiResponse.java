package edu.sustech.hpc.result;

import edu.sustech.hpc.model.vo.RoleVo;
import lombok.AllArgsConstructor;
import lombok.Data;

@SuppressWarnings({"rawtypes", "unused"})
@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public static ApiResponse success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "OK", data);
    }

    public static ApiResponse badRequest(String message){
        return new ApiResponse<>(400,message);
    }

    public static ApiResponse internalServerError(){
        return internalServerError("Internal Server Error");
    }

    public static ApiResponse internalServerError(String message){
        return new ApiResponse(500,message);
    }

    public static ApiResponse<RoleVo> error(String roleNotFound) {

        return null;
    }
}
