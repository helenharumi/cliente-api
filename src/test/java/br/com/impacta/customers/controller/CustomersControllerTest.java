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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.impacta.customers.controller.factory.CustomersEntityTestFactory;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import br.com.impacta.customers.dto.CustomersDTO;
import br.com.impacta.customers.dto.CustomersInsertDTO;
import br.com.impacta.customers.entity.CustomersEntity;
import br.com.impacta.customers.repository.CustomersRepository;
import br.com.impacta.customers.service.CustomersService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
	public  String URL_CUSTOMER = "/v1/customers";
	private String token;
	
	@BeforeEach
	void setUp() throws Exception {
		
		operatorUserName = "alex@gmail.com";
		operatorPassword = "123456";
		token = obtainAccessToken(operatorUserName, operatorPassword);

	}

	@Test
	public void post_saveCustomerAndReturnTheResource201() throws Exception {

		CustomersInsertDTO dto = CustomersEntityTestFactory.createCustomerInsertDTO("Client");
		objectMapper = new ObjectMapper();
		ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(dto);

		when(service.save(any())).thenReturn(CustomersEntityTestFactory.createCustomer(1L, "Client"));
		
		ResultActions result = mockMvc.perform(post(URL_CUSTOMER)
				.header("Authorization", "Bearer " + token)
				.contentType(APPLICATION_JSON_UTF8)
				.content(requestJson));

		result.andExpect(status().isCreated());
	
	}

	@Test
	public void get_findCustomerByIdSuccess() throws Exception {
		Long clientId = 100L;

		Optional<CustomersEntity> customer = Optional.ofNullable(CustomersEntityTestFactory.createCustomer(clientId, "Client"));

		when(repository.findById(anyLong())).thenReturn(customer);

		mockMvc.perform(get(URL_CUSTOMER + "/{id}", clientId)
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void get_findCustomerByIdNotFound() throws Exception {
		Long clientId = 100L;

		mockMvc.perform(get(URL_CUSTOMER + "/{id}", clientId)
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound());
	}

	@Test
	public void get_findCustomerByNameSuccess() throws Exception {
		String clientName = "Client";
		List<CustomersEntity> listCustomer = new ArrayList<>();
		listCustomer.add(CustomersEntityTestFactory.createCustomer(1L, clientName));

		when(repository.findByNameIgnoreCase(anyString())).thenReturn(listCustomer);

		mockMvc.perform(get(URL_CUSTOMER + "/findByName/{name}", clientName)
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsBytes(listCustomer)))
				.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void get_findCustomerByNameNotFound() throws Exception {
		String clientName = "Client";

		when(repository.findByNameIgnoreCase(anyString())).thenReturn(null);

		mockMvc.perform(get(URL_CUSTOMER + "/findByName/{name}", clientName)
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound());
	}

	@Test
	public void put_updatesAndReturnsUpdatedObjWith204() throws Exception {

		CustomersEntity client = CustomersEntityTestFactory.createCustomer(1L, "Client");
		CustomersDTO dto = CustomersEntityTestFactory.createCustomerDTO(client.getId(), client.getName());

		Optional<CustomersEntity> customersEntity = Optional.ofNullable(client);

		when(repository.findById(Mockito.anyLong())).thenReturn(customersEntity);
		when(service.save(Mockito.any(CustomersEntity.class))).thenReturn(client);

		ResultActions result = mockMvc.perform(put(URL_CUSTOMER + "/{id}", client.getId())
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(dto))
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8));
		
					
		result.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void post_submitsInvalidCustomer_WithEmptyMake_Returns400() throws Exception {

		CustomersEntity client = new CustomersEntity();
		CustomersDTO dto = new CustomersDTO();

		when(service.save(any(CustomersEntity.class))).thenReturn(client);

		ResultActions result = mockMvc.perform(post(URL_CUSTOMER)
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(dto))
				.contentType(APPLICATION_JSON_UTF8));

		result.andExpect(status().isBadRequest());

	}

	@Test
	public void get_allCustomers_returnsOkWithListOfCustomers() throws Exception {

		List<CustomersEntity> list = List.of(
				CustomersEntityTestFactory.createCustomer(1L, "Client"),
				CustomersEntityTestFactory.createCustomer(2L, "Client2")
		);

		Page<CustomersEntity> pages = new PageImpl<>(list);
		
		PageRequest pageRequest = PageRequest.of(0, 12, Direction.valueOf("ASC"), "name");

		when(service.findAllPaged(pageRequest)).thenReturn(pages);

		ResultActions result = mockMvc.perform(get(URL_CUSTOMER)
				.contentType(APPLICATION_JSON_UTF8));

		result.andExpect(status().isOk());

	}

	@Test
	public void deleteCustomer() throws Exception {
		Long clientId = 100L;

		Optional<CustomersEntity> cust = Optional.ofNullable(CustomersEntityTestFactory.createCustomer(1L, "Client"));

		when(repository.findById(Mockito.anyLong())).thenReturn(cust);
		doNothing().when(repository).deleteById(clientId);

		ResultActions result = mockMvc.perform(
				delete(URL_CUSTOMER + "/{id}", clientId)
				.header("Authorization", "Bearer " + token));

		result.andExpect(status().isNoContent());
	}

	public String obtainAccessToken(String username, String password) throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", clientId);
		params.add("username", username);
		params.add("password", password);


		ResultActions result = mockMvc.perform(post("/oauth/token")
						.params(params).with(httpBasic(clientId, clientSecret))
						.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8));

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}

}
