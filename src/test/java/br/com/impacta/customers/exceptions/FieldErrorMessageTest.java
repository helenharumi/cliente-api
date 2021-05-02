package br.com.impacta.customers.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldErrorMessageTest {

    @Test
    public void shouldReturnFieldErrorMessage(){
        FieldErrorMessage fieldErrorMessage = new FieldErrorMessage();
        fieldErrorMessage.setMessage("error");
        fieldErrorMessage.setFieldName("id");

        assertEquals("error", fieldErrorMessage.getMessage());
        assertEquals("id", fieldErrorMessage.getFieldName());
    }
}