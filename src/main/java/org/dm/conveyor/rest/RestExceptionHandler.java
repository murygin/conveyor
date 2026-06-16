package org.dm.conveyor.rest;

import org.dm.conveyor.model.JobNotFoundException;
import org.dm.conveyor.model.UUIDNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(JobNotFoundException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public String handleJobNotFoundException(JobNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UUIDNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public String handleUUIDNotValidException(UUIDNotValidException e) {
        return e.getMessage();
    }

}
