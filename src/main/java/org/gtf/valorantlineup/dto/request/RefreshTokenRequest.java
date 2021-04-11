package org.gtf.valorantineup.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank
    @Size(min=128, max=128)
    private String refreshToken;

    //never put FINAL in DTO.

}
