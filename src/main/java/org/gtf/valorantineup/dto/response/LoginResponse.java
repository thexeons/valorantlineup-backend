package org.gtf.valorantineup.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Object data;
    private List<String> roles;
}
