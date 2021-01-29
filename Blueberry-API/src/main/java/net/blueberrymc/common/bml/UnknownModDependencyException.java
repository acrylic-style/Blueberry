package net.blueberrymc.common.bml;

public class UnknownModDependencyException extends RuntimeException {
    public UnknownModDependencyException(String message) {
        super(message);
    }

    public UnknownModDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownModDependencyException(Throwable cause) {
        super(cause);
    }
}
