package org.gtf.valorantlineup.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
	@NotBlank
	private String userIdentifier;

	@NotBlank
	@Size(min = 6, max = 20)
	private String password;
}
