package br.com.impacta.customers.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.impacta.customers.util.LocalDateTimeToStringConverter;
import br.com.impacta.customers.util.StringToLocalDatetimeConverter;

public class CustomersDTO {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	@NotNull(message = "Name cannot be null")
	@NotBlank(message = "Name cannot be empty")
	@Size(min = 1, max = 200, message = "The name must be between 1 and 200 characters")
	private String name;

	@NotNull(message = "Birth date is mandatory")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@JsonSerialize(converter = LocalDateTimeToStringConverter.class)
	@JsonDeserialize(converter = StringToLocalDatetimeConverter.class)
	private LocalDateTime birthDate;

	public CustomersDTO() {
	}

	public CustomersDTO(Long id , String name, LocalDateTime birthDate) {
		this.id = id;
		this.name = name;
		this.birthDate = birthDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDateTime birthDate) {
		this.birthDate = birthDate;
	}

}
