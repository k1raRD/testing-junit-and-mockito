package com.k1rard.test.springbootapp.springboottest.exceptions;

public class DineroInsificienteException extends RuntimeException{
    public DineroInsificienteException(String message) {
        super(message);
    }
}
