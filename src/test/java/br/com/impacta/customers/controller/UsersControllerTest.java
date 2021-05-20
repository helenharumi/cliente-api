package br.com.impacta.customers.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.List;

import br.com.impacta.customers.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import br.com.impacta.customers.repository.UserRepository;
import br.com.impacta.customers.service.UserService;


public class UsersControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService service;

	@MockBean
	private UserRepository repository;

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
	

	public void get_allUsers_returnsOkWithListOfUsers() throws Exception {
		
		String acessToken = obtainAccessToken(operatorUserName, operatorPassword);
		System.out.println(acessToken);

		UserEntity user1 = new UserEntity(1L, "Teste", "teste1", "teste@gmail.com", "");
		UserEntity user2 = new UserEntity(1L, "Teste", "teste1", "teste@gmail.com", "");
		

		List<UserEntity> list = List.of(user1, user2);
		Page<UserEntity> pages = new PageImpl<UserEntity>(list);
		
		PageRequest pageRequest = PageRequest.of(0, 12, Direction.valueOf("ASC"), "firtName");

		doNothing().when(repository.findAll(pageRequest));
		when(service.findAllPaged(pageRequest)).thenReturn(pages);
		
		ResultActions result = mockMvc.perform(get("/users")
				.header("Authorization", "Bearer " + acessToken)
				.contentType(APPLICATION_JSON_UTF8));
		
		//result.andExpect(status().isOk());

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
