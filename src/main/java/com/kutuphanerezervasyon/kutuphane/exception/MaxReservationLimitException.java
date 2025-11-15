package com.kutuphanerezervasyon.kutuphane.exception;

public class MaxReservationLimitException extends RuntimeException {
    public MaxReservationLimitException(String message) {
        super(message);
    }
}
