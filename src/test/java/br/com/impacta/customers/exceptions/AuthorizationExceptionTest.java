package br.com.impacta.customers.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AuthorizationExceptionTest {

    @Test
    public void shouldReturnAuthorizationExceptionMessage(){
        AuthorizationException authorizationException = new AuthorizationException("error");

        Assertions.assertEquals("error", authorizationException.getMessage());

    }

    @Test
    public void shouldReturnAuthorizationException(){
        AuthorizationException authorizationException = new AuthorizationException("error", new Throwable());

        Assertions.assertEquals("error", authorizationException.getMessage());

    }
}