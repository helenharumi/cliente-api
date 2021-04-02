package br.com.impacta.clientes.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.util.StdConverter;

public class LocalDateTimeToStringConverter extends StdConverter<LocalDateTime, String> {
	static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

	@Override
	public String convert(LocalDateTime value) {
		return value.format(DATE_FORMATTER);
	}

}
