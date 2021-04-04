package br.com.impacta.customers.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.impacta.customers.dto.CustomersDTO;
import br.com.impacta.customers.entity.CustomersEntity;
import br.com.impacta.customers.service.CustomersService;

@SpringBootTest(properties = { "spring.jpa.hibernate.ddl-auto=create-drop", "spring.liquibase.enabled=false",
		"spring.flyway.enabled=false" })
@AutoConfigureMockMvc
public class ClienteServiceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	CustomersService service;
	
	
	@Test
	public void post_saveCustomerAndReturnTheResource201() throws Exception {
		
		CustomersEntity cliente = new CustomersEntity();
		cliente.setId(1l);
		cliente.setName("Renan");
		cliente.setBirthDate(LocalDateTime.of(1992, 07, 20, 23, 59));

		CustomersDTO dto = new CustomersDTO(cliente.getId(), cliente.getName(), cliente.getBirthDate());

		when(service.save(Mockito.any(CustomersEntity.class))).thenReturn(cliente);

		mockMvc.perform(post("/v1/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isCreated());
	}
	
	@Test
	public void get_findCustomerByIdSucess() throws Exception {
		Long idCliente = 1l;
		CustomersEntity cliente = new CustomersEntity(1l, "Maria", LocalDateTime.now());
		CustomersDTO dto = new CustomersDTO(cliente.getId(), cliente.getName(), cliente.getBirthDate());
		
		when(service.findById(Mockito.anyLong())).thenReturn(cliente);
		
		mockMvc.perform(get("/v1/customers/{id}", idCliente)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(dto)))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void get_findCustomerByNameSucess() throws Exception {
		String nameCli = "Maria";
		List<CustomersEntity> listCustomer = new ArrayList<>();
		CustomersEntity client = new CustomersEntity(1l, "Maria", LocalDateTime.now());
		listCustomer.add(client);
		
		when(service.findByName(Mockito.anyString())).thenReturn(listCustomer);
		
		mockMvc.perform(get("/v1/customers/findByName/{name}", nameCli)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(listCustomer)))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void put_updatesAndReturnsUpdatedObjWith204() throws Exception {
		CustomersEntity cliente = new CustomersEntity(1l, "Maria", LocalDateTime.now());
		CustomersDTO dto = new CustomersDTO(cliente.getId(), cliente.getName(), cliente.getBirthDate());

		when(service.update(cliente)).thenReturn(cliente);

		mockMvc.perform(put("/v1/customers/1", cliente)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(objectMapper.writeValueAsBytes(dto)))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void post_submitsInvalidCustomer_WithEmptyMake_Returns400() throws Exception {
		CustomersEntity cliente = new CustomersEntity();
		CustomersDTO dto = new CustomersDTO();

		when(service.save(Mockito.any(CustomersEntity.class))).thenReturn(cliente);

		mockMvc.perform(post("/v1/customers/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void get_allCustomers_returnsOkWithListOfCustomers() throws Exception {

		CustomersEntity c1 = new CustomersEntity();
		CustomersEntity c2 = new CustomersEntity();

		List<CustomersEntity> list = List.of(c1, c2);

		Mockito.when(service.findAll()).thenReturn(list);

		mockMvc.perform(get("/v1/customers/list")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void deleteCustomer() throws Exception {
		Long idCliente = 100l;
		ResultActions result = mockMvc.perform(delete("/v1/customers/{id}", idCliente)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());
	}
}
