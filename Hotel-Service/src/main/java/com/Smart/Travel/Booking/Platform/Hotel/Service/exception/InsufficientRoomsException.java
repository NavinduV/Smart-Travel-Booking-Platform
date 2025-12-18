package com.Smart.Travel.Booking.Platform.Hotel.Service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientRoomsException extends RuntimeException {
    
    public InsufficientRoomsException(String message) {
        super(message);
    }

    public InsufficientRoomsException(String message, Throwable cause) {
        super(message, cause);
    }
}
