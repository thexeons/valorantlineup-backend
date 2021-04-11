package org.gtf.valorantineup.exception;


import org.gtf.valorantineup.dto.response.ErrorResponse;
import org.gtf.valorantineup.dto.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public abstract class AbstractRequestHandler {

    //handler to handle standard payload format

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ResponseEntity<Object> getResult(HttpStatus... statuses) {
        SuccessResponse successResult = new SuccessResponse();
        try {
            Object obj = processRequest();
            if (obj != null) {
                successResult.setMessage(HttpStatus.OK.getReasonPhrase());
                successResult.setPayload(obj);
            }else {
                successResult.setMessage(HttpStatus.NO_CONTENT.getReasonPhrase());
                successResult.setPayload(null);
            }
        } catch (GTFException e) {
            log.error(e.getLocalizedMessage(),e.getCause());
            ErrorResponse errorResult = new ErrorResponse();
            errorResult.setTimestamp(new Date().getTime());
            errorResult.setMessage(e.getMessage());
            errorResult.setHttpCode(String.valueOf(e.getCode().value()));
            errorResult.setHttpCodeMessage(e.getCode().getReasonPhrase());
            return ResponseEntity.status(e.getCode()).body(errorResult);
        }
        if(statuses.length<1) {
            return ResponseEntity.ok(successResult);
        }
        else
        {
        return ResponseEntity.status(statuses[1]).body(successResult);
        }
    }

    //Abstract method to retrieve return Object from a service
    public abstract Object processRequest();

}
