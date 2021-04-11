package org.gtf.valorantlineup.dto.response;

import lombok.Data;

@Data
public class SuccessResponse {

    private String message;
    private Object payload;

    public SuccessResponse() {
    }

    public SuccessResponse(String message, Object result) {
        this.message = message;
        this.payload = payload;
    }
}
