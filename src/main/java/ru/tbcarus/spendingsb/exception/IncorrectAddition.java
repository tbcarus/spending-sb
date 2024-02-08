package ru.tbcarus.spendingsb.exception;

import ru.tbcarus.spendingsb.model.ErrorType;

public class IncorrectAddition extends RuntimeException {
    public ErrorType errorType;

    public IncorrectAddition(ErrorType errorType) {
        super();
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}