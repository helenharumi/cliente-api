package br.com.impacta.clientes.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ClienteInsertDTO {

	@JsonProperty("nome")
	@NotNull(message = "Name cannot be null")
	@NotBlank(message = "Name cannot be empty")
	@Size(min = 1, max = 200, message = "The name must be between 1 and 200 characters")
	private String nome;

	@NotNull(message = "Birth date is mandatory")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@JsonSerialize(converter = LocalDateTimeToStringConverter.class)
	@JsonDeserialize(converter = StringToLocalDatetimeConverter.class)
	private LocalDateTime dataNascimento;

	public ClienteInsertDTO() {
	}

	public ClienteInsertDTO(String nome, LocalDateTime dataNascimento) {
		this.nome = nome;
		this.dataNascimento = dataNascimento;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public LocalDateTime getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDateTime dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
}
