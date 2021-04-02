package br.com.impacta.clientes.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ClienteInsertDTO {
	
	@NotNull(message = "Campo nome é obrigatório")
	@JsonProperty("nome")
	private String nome;

	@NotNull(message = "Campo data nascimento é obrigatório")
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
