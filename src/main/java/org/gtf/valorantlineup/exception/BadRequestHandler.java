package org.gtf.valorantlineup.exception;

import org.gtf.valorantlineup.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class BadRequestHandler {

    //Validation error handler

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> processHandler(MethodArgumentNotValidException ex) {

        //Append error via StringBuilder

        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder errors = new StringBuilder("Error: ");
        for (int i = 0; i < fieldErrors.size(); i++) {
            log.info(fieldErrors.get(i).getField() + " " + fieldErrors.get(i).getDefaultMessage());
            errors.append(fieldErrors.get(i).getField() + " " + fieldErrors.get(i).getDefaultMessage());
            if(i == fieldErrors.size()-1)
            {
                errors.append(".");
            }
            else
            {
                errors.append(", ");
            }
        }

        //Build request body

        ErrorResponse errorResult = new ErrorResponse();
        errorResult.setHttpCode(String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errorResult.setHttpCodeMessage(HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorResult.setMessage(errors.toString());
        errorResult.setTimestamp(new Date().getTime());
        return ResponseEntity.badRequest().body(errorResult);
    }
}
