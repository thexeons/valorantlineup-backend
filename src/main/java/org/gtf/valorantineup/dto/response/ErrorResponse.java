package org.gtf.valorantineup.dto.response;

import lombok.Data;

@Data
public class ErrorResponse {

    private long timestamp;
    private String message;
    private String httpCode;
    private String httpCodeMessage;

    public ErrorResponse() {
    }

    public ErrorResponse(long timestamp, String message, String detail, String httpCodeMessage) {
        this.timestamp = timestamp;
        this.message = message;
        this.httpCode = detail;
        this.httpCodeMessage = httpCodeMessage;
    }
}
