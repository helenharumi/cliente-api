package br.com.impacta.clientes;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.impacta.clientes.dto.ClienteDTO;
import br.com.impacta.clientes.entity.ClienteEntity;
import br.com.impacta.clientes.service.ClienteService;

@SpringBootTest(properties = { "spring.jpa.hibernate.ddl-auto=create-drop", "spring.liquibase.enabled=false",
		"spring.flyway.enabled=false" })
@AutoConfigureMockMvc
public class ClienteServiceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	ClienteService service;

	@Test
	public void post_saveCustomerAndReturnTheResource201() throws Exception {
		ClienteEntity dados = new ClienteEntity();
		dados.setId(1l);
		dados.setNome("Renan");
		dados.setDataNascimento(LocalDateTime.of(1992, 07, 20, 23, 59));

		ClienteDTO dto = new ClienteDTO(dados.getId(), dados.getNome(), dados.getDataNascimento());

		when(service.incluir(Mockito.any(ClienteDTO.class))).thenReturn(dto);

		mockMvc.perform(post("/cliente").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated());
	}

	@Test
	public void put_updatesAndReturnsUpdatedObjWith204() throws Exception {
		ClienteDTO cliente = new ClienteDTO(1l, "Maria", LocalDateTime.now());

		when(service.update(cliente)).thenReturn(cliente);

		mockMvc.perform(put("/cliente/1", cliente).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
				.content(objectMapper.writeValueAsBytes(cliente))).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void post_submitsInvalidCustomer_WithEmptyMake_Returns400() throws Exception {
		ClienteDTO dto = new ClienteDTO();

		when(service.incluir(Mockito.any(ClienteDTO.class))).thenReturn(dto);

		mockMvc.perform(
				post("/cliente/").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void get_allCustomers_returnsOkWithListOfCustomers() throws Exception {

		ClienteEntity c1 = new ClienteEntity();
		ClienteEntity c2 = new ClienteEntity();

		List<ClienteEntity> list = List.of(c1, c2);

		// Mocking out the vehicle service
		Mockito.when(service.findAll()).thenReturn(list);

		mockMvc.perform(get("/cliente/list").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void deleteCustomer() throws Exception {

		Long idCliente = 100l;

		ClienteService serviceSpy = Mockito.spy(service);

		Mockito.doNothing().when(serviceSpy).deleteById(idCliente);

		mockMvc.perform(delete("/cliente/" + idCliente).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		verify(service, times(1)).deleteById(idCliente);
	}
}
