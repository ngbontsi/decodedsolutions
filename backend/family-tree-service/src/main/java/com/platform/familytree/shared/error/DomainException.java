package com.platform.familytree.shared.error;

public class DomainException extends RuntimeException {

    private final ErrorCode code;

    public DomainException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode code() {
        return code;
    }

    public static DomainException notFound(String what, Object id) {
        return new DomainException(ErrorCode.NOT_FOUND, what + " not found: " + id);
    }

    public static DomainException conflict(String message) {
        return new DomainException(ErrorCode.CONFLICT, message);
    }

    public static DomainException rule(String message) {
        return new DomainException(ErrorCode.RULE_VIOLATION, message);
    }

    public static DomainException invalid(String message) {
        return new DomainException(ErrorCode.INVALID_ARGUMENT, message);
    }
}
