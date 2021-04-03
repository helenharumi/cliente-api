package br.com.impacta.clientes.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidatorErrors extends DefaultError {

	private static final long serialVersionUID = 5168544822398320044L;

	private List<FieldErrorMessage> list = new ArrayList<>();

	public ValidatorErrors(int status, String message, Long timeStamp) {
		super(status, message, timeStamp);
	}

	public List<FieldErrorMessage> getErrors() {
		return list;
	}

	public void addError(String fieldName, String message) {
		list.add(new FieldErrorMessage(fieldName, message));
	}

}
