package edu.sustech.hpc.exceptions;

public class DuplicateDeviceException extends RuntimeException {
    public DuplicateDeviceException(String message) {
        super(message);
    }
}
