package edu.sustech.hpc.exceptions;

public class PromQLValidationException  extends RuntimeException {
    public PromQLValidationException(String message) {
        super(message);
    }

    public PromQLValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
