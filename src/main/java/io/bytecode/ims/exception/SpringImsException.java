package io.bytecode.ims.exception;

public class SpringImsException extends RuntimeException {

    public SpringImsException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }

    public SpringImsException(String exMessage) {
        super(exMessage);
    }
}