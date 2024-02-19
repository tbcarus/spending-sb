package ru.tbcarus.spendingsb.exception;

import ru.tbcarus.spendingsb.model.ErrorType;

public class BadRegistrationRequest extends RuntimeException {
    public ErrorType errorType;

    public BadRegistrationRequest(ErrorType errorType) {
        super();
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}