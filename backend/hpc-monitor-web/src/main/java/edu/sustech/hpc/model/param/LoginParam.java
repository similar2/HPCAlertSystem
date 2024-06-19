package edu.sustech.hpc.model.param;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginParam {

    @Email
    @NotEmpty
    private String email;

    @Size(min=8, max=20, message="长度必须在8-20之间")
    private String password;
}
