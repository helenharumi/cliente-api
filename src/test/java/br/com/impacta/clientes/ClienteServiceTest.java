package br.com.impacta.clientes;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
		ClienteEntity cliente = new ClienteEntity();
		cliente.setId(1l);
		cliente.setNome("Renan");
		cliente.setDataNascimento(LocalDateTime.of(1992, 07, 20, 23, 59));

		ClienteDTO dto = new ClienteDTO(cliente.getId(), cliente.getNome(), cliente.getDataNascimento());

		when(service.save(Mockito.any(ClienteEntity.class))).thenReturn(cliente);

		mockMvc.perform(post("/cliente").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated());
	}

	@Test
	public void put_updatesAndReturnsUpdatedObjWith204() throws Exception {
		ClienteEntity cliente = new ClienteEntity(1l, "Maria", LocalDateTime.now());
		ClienteDTO dto = new ClienteDTO(cliente.getId(), cliente.getNome(), cliente.getDataNascimento());

		when(service.update(cliente)).thenReturn(cliente);

		mockMvc.perform(put("/v1/cliente/1", cliente).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")
				.content(objectMapper.writeValueAsBytes(dto))).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void post_submitsInvalidCustomer_WithEmptyMake_Returns400() throws Exception {
		ClienteEntity cliente = new ClienteEntity();
		ClienteDTO dto = new ClienteDTO();

		when(service.save(Mockito.any(ClienteEntity.class))).thenReturn(cliente);

		mockMvc.perform(
				post("/v1/cliente/").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void get_allCustomers_returnsOkWithListOfCustomers() throws Exception {

		ClienteEntity c1 = new ClienteEntity();
		ClienteEntity c2 = new ClienteEntity();

		List<ClienteEntity> list = List.of(c1, c2);

		// Mocking out the vehicle service
		Mockito.when(service.findAll()).thenReturn(list);

		mockMvc.perform(get("/v1/cliente/list").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void deleteCustomer() throws Exception {

		Long idCliente = 100l;

		ClienteService serviceSpy = Mockito.spy(service);

		Mockito.doNothing().when(serviceSpy).deleteById(idCliente);

		mockMvc.perform(delete("/v1/cliente/" + idCliente).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		verify(service, times(1)).deleteById(idCliente);
	}
}	