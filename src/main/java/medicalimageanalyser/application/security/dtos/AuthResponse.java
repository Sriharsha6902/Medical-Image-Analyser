package medicalimageanalyser.application.security.dtos;

import lombok.Data;

@Data
public class AuthResponse {
    private String username;
    private String response;
}
