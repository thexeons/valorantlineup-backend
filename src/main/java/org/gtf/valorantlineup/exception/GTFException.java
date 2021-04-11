package org.gtf.valorantlineup.exception;

import org.springframework.http.HttpStatus;

public class GTFException extends RuntimeException {

    private HttpStatus code;

    public GTFException(String message) {
        super(message);
    }

    public GTFException(HttpStatus code, String message) {
        super(message);
        this.code = code;
    }

    public HttpStatus getCode() {
        return code;
    }
}
