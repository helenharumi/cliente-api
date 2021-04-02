package br.com.impacta.clientes.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ClienteDTO {

	@JsonProperty("id")
	private Long id;
	@NotNull(message = "Campo nome é obrigatório")
	@JsonProperty("nome")
	private String nome;

	@NotNull(message = "Campo data nascimento é obrigatório")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@JsonSerialize(converter = LocalDateTimeToStringConverter.class)
	@JsonDeserialize(converter = StringToLocalDatetimeConverter.class)
	private LocalDateTime dataNascimento;

	public ClienteDTO() {
	}

	public ClienteDTO(Long id, String nome, LocalDateTime dataNascimento) {
		this.id = id;
		this.nome = nome;
		this.dataNascimento = dataNascimento;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
