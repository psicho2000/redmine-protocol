package de.psicho.redmine.protocol.controller;

public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 627866244450477099L;

    public ValidationException(String message) {
        super(message);
    }

}
