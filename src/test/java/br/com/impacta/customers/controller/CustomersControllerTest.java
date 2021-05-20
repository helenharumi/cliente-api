package br.com.impacta.customers.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import br.com.impacta.customers.dto.CustomersDTO;
import br.com.impacta.customers.dto.CustomersInsertDTO;
import br.com.impacta.customers.entity.CustomersEntity;
import br.com.impacta.customers.repository.CustomersRepository;
import br.com.impacta.customers.service.CustomersService;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomersControllerTest {

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
	
	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;
	
	private String operatorUserName;
	private String operatorPassword;
	
	@BeforeEach
	void setUp() throws Exception {
		
		operatorUserName = "maria@gmail.com";
		operatorPassword = "123456";

	}

	@Test
	public void post_saveCustomerAndReturnTheResource201() throws Exception {
		
		String acessToken = obtainAccessToken(operatorUserName, operatorPassword);

		CustomersInsertDTO dto = new CustomersInsertDTO(mockCustomersEntity().getName(), mockCustomersEntity().getBirthDate());

		objectMapper = new ObjectMapper();
		ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(dto);

		when(service.save(any())).thenReturn(mockCustomersEntity());
		
		ResultActions result = mockMvc.perform(post("/v1/customers")
				.header("Authorization", "Bearer " + acessToken)
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));
		
		
		result.andExpect(status().isCreated());
	
	}

	@Test
	public void get_findCustomerByIdSucess() throws Exception {
		Long idCliente = 100L;

		Optional<CustomersEntity> cust = Optional.ofNullable(mockCustomersEntity());

		when(repository.findById(anyLong())).thenReturn(cust);

		mockMvc.perform(get("/v1/customers/{id}", idCliente)
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void get_findCustomerByIdNotFound() throws Exception {
		Long idCliente = 100L;

		mockMvc.perform(get("/v1/customers/{id}", idCliente)
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound());
	}

	@Test
	public void get_findCustomerByNameSucess() throws Exception {
		String nameCli = "Maria";
		List<CustomersEntity> listCustomer = new ArrayList<>();
		CustomersEntity client = new CustomersEntity(1L, "Maria", LocalDateTime.now());
		listCustomer.add(client);

		when(repository.findByNameIgnoreCase(anyString())).thenReturn(listCustomer);

		mockMvc.perform(get("/v1/customers/findByName/{name}", nameCli)
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsBytes(listCustomer)))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void get_findCustomerByNameNotFound() throws Exception {
		String nameCli = "Maria";

		when(repository.findByNameIgnoreCase(anyString())).thenReturn(null);

		mockMvc.perform(get("/v1/customers/findByName/{name}", nameCli)
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound());
	}

	@Test
	public void put_updatesAndReturnsUpdatedObjWith204() throws Exception {
		String acessToken = obtainAccessToken(operatorUserName, operatorPassword);
		
		CustomersEntity cliente = new CustomersEntity(1L, "Maria", LocalDateTime.now());
		CustomersDTO dto = new CustomersDTO(cliente.getId(), cliente.getName(), cliente.getBirthDate());

		Optional<CustomersEntity> cust = Optional.ofNullable(cliente);

		when(repository.findById(Mockito.anyLong())).thenReturn(cust);
		when(service.save(Mockito.any(CustomersEntity.class))).thenReturn(cliente);

		ResultActions result = mockMvc.perform(put("/v1/customers/{id}", cliente.getId())
				.header("Authorization", "Bearer " + acessToken)
				.content(objectMapper.writeValueAsString(dto))
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8));
		
					
		result.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void post_submitsInvalidCustomer_WithEmptyMake_Returns400() throws Exception {
		
		String acessToken = obtainAccessToken(operatorUserName, operatorPassword);
		
		CustomersEntity cliente = new CustomersEntity();
		CustomersDTO dto = new CustomersDTO();

		when(service.save(Mockito.any(CustomersEntity.class))).thenReturn(cliente);

		ResultActions result = mockMvc.perform(post("/v1/customers/")
				.header("Authorization", "Bearer " + acessToken)
				.content(objectMapper.writeValueAsString(dto))
				.contentType(APPLICATION_JSON_UTF8));
				
		
		result.andExpect(status().isBadRequest());
				
	}

	@Test
	public void get_allCustomers_returnsOkWithListOfCustomers() throws Exception {

		CustomersEntity c1 = new CustomersEntity();
		CustomersEntity c2 = new CustomersEntity();

		List<CustomersEntity> list = List.of(c1, c2);
		Page<CustomersEntity> pages = new PageImpl<>(list);
		
		PageRequest pageRequest = PageRequest.of(0, 12, Direction.valueOf("ASC"), "name");

		Mockito.when(service.findAllPaged(pageRequest)).thenReturn(pages);

		ResultActions result = mockMvc.perform(get("/v1/customers/")
				.contentType(APPLICATION_JSON_UTF8))
				;
		
		result.andExpect(status().isOk());

	}

	@Test
	public void deleteCustomer() throws Exception {
		Long idCliente = 100L;
		
		String acessToken = obtainAccessToken(operatorUserName, operatorPassword);

		Optional<CustomersEntity> cust = Optional.ofNullable(mockCustomersEntity());

		when(repository.findById(Mockito.anyLong())).thenReturn(cust);
		doNothing().when(repository).deleteById(100L);

		ResultActions result = mockMvc.perform(
				delete("/v1/customers/{id}", idCliente)
				.header("Authorization", "Bearer " + acessToken));
	
		result.andExpect(status().isNoContent());
	}

	private CustomersEntity mockCustomersEntity() {
		CustomersEntity cliente = new CustomersEntity();
		cliente.setId(100L);
		cliente.setName("Renan");
		cliente.setBirthDate(LocalDateTime.of(1992, 07, 20, 23, 59));
		return cliente;
	}


	private String obtainAccessToken(String username, String password) throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", clientId);
		params.add("username", username);
		params.add("password", password);


		ResultActions result = mockMvc
				.perform(post("/oauth/token")
						.params(params).with(httpBasic(clientId, clientSecret))
						.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8));

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}
}
