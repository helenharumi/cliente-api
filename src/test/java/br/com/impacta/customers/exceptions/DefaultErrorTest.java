package br.com.impacta.customers.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultErrorTest {

    @Test
    public void shouldReturnDefaultError(){
        DefaultError defaultError = new DefaultError(500, "an exception was thrown", 1L);
        defaultError.setStatus(500);
        defaultError.setMessage("an exception was thrown");
        defaultError.setTimeStamp(1L);

        assertEquals(500, defaultError.getStatus());
        assertEquals("an exception was thrown", defaultError.getMessage());
        assertEquals(1L, defaultError.getTimeStamp());
    }
}
