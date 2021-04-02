package br.com.impacta.clientes.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.util.StdConverter;

public class StringToLocalDatetimeConverter extends StdConverter<String, LocalDateTime> {

	@Override
	public LocalDateTime convert(String value) {
		return LocalDateTime.parse(value, LocalDateTimeToStringConverter.DATE_FORMATTER);
	}
}
