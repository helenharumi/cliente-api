package br.com.impacta.customers.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import br.com.impacta.customers.dto.CustomersDTO;
import br.com.impacta.customers.dto.CustomersInsertDTO;
import br.com.impacta.customers.entity.CustomersEntity;
import br.com.impacta.customers.repository.CustomersRepository;
import br.com.impacta.customers.service.CustomersService;

@SpringBootTest()
@AutoConfigureMockMvc
public class ClienteServiceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CustomersService service;

	@MockBean
	private CustomersRepository repository;

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Test
	public void post_saveCustomerAndReturnTheResource201() throws Exception {

		CustomersInsertDTO dto = new CustomersInsertDTO(mockCustomersEntity().getName(), mockCustomersEntity().getBirthDate());

		objectMapper = new ObjectMapper();
		ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(dto);

		when(service.save(Mockito.any())).thenReturn(mockCustomersEntity());

		mockMvc.perform(post("/v1/customers")
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson))
				.andExpect(status().isCreated()).andReturn();
	}

	@Test
	public void get_findCustomerByIdSucess() throws Exception {
		Long idCliente = 100l;

		Optional<CustomersEntity> cust = Optional.ofNullable(mockCustomersEntity());

		when(repository.findById(Mockito.anyLong())).thenReturn(cust);

		mockMvc.perform(get("/v1/customers/{id}", idCliente)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void get_findCustomerByIdNotFound() throws Exception {
		Long idCliente = 100l;

		mockMvc.perform(get("/v1/customers/{id}", idCliente)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void get_findCustomerByNameSucess() throws Exception {
		String nameCli = "Maria";
		List<CustomersEntity> listCustomer = new ArrayList<>();
		CustomersEntity client = new CustomersEntity(1l, "Maria", LocalDateTime.now());
		listCustomer.add(client);

		when(repository.findByNameIgnoreCase(Mockito.anyString())).thenReturn(listCustomer);

		mockMvc.perform(get("/v1/customers/findByName/{name}", nameCli)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(listCustomer)))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void get_findCustomerByNameIllegalArgNameEmpty() throws Exception {
		mockMvc.perform(get("/v1/customers/findByName/{name}", "")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
//	@Test
//	public void get_findCustomerByNameIllegalArgNameNull() throws Exception {
//		mockMvc.perform(get("/v1/customers/findByName/{name}", null)
//				.contentType(MediaType.APPLICATION_JSON_VALUE)
//				.accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isBadRequest());
//	}
//	
	@Test
	public void get_findCustomerByNameNotFound() throws Exception {
		String nameCli = "Maria";

		when(repository.findByNameIgnoreCase(Mockito.anyString())).thenReturn(null);

		mockMvc.perform(get("/v1/customers/findByName/{name}", nameCli)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	public void put_updatesAndReturnsUpdatedObjWith204() throws Exception {
		CustomersEntity cliente = new CustomersEntity(1l, "Maria", LocalDateTime.now());
		CustomersDTO dto = new CustomersDTO(cliente.getId(), cliente.getName(), cliente.getBirthDate());

		Optional<CustomersEntity> cust = Optional.ofNullable(cliente);

		when(repository.findById(Mockito.anyLong())).thenReturn(cust);
		when(service.save(Mockito.any(CustomersEntity.class))).thenReturn(cliente);

		mockMvc.perform(put("/v1/customers/1", cliente)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(objectMapper.writeValueAsBytes(dto)))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void put_updatesAndReturnsUpdatedObjWith400() throws Exception {
		Long idCliente = 100l;

		mockMvc.perform(put("/v1/customers/1", idCliente)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
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

		Optional<CustomersEntity> cust = Optional.ofNullable(mockCustomersEntity());

		when(repository.findById(Mockito.anyLong())).thenReturn(cust);
		doNothing().when(repository).deleteById(100l);

		mockMvc.perform(delete("/v1/customers/{id}", idCliente))
				.andExpect(status().isNoContent());
	}

	private CustomersEntity mockCustomersEntity() {
		CustomersEntity cliente = new CustomersEntity();
		cliente.setId(100l);
		cliente.setName("Renan");
		cliente.setBirthDate(LocalDateTime.of(1992, 07, 20, 23, 59));
		return cliente;
	}
}
