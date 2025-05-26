package medicalimageanalyser.application.security.dtos;

import lombok.Data;

@Data
public class LoginRequest {
    private String userCred;
    private String password;
}
