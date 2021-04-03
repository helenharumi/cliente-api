package br.com.impacta.clientes.exceptions;

import java.io.Serializable;

public class FieldErrorMessage implements Serializable {

	private static final long serialVersionUID = 4209617137906724142L;
	
	private String fieldName; 
	private String message;
	
	
	public FieldErrorMessage() {
		super();
	}

	public FieldErrorMessage(String fieldName, String message) {
		super();
		this.fieldName = fieldName;
		this.message = message;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
