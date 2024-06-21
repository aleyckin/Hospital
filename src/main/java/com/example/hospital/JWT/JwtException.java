package com.example.hospital.JWT;

public class JwtException extends RuntimeException{
    public JwtException(Throwable throwable) {
        super(throwable);
    }

    public JwtException(String message) {
        super(message);
    }
}
