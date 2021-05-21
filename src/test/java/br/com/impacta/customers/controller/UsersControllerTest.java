package br.com.impacta.customers.controller;

import static org.hamcrest.Matchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import br.com.impacta.customers.controller.factory.CustomersEntityTestFactory;
import br.com.impacta.customers.controller.factory.UserEntityTestFactory;
import br.com.impacta.customers.dto.CustomersDTO;
import br.com.impacta.customers.dto.UserDTO;
import br.com.impacta.customers.dto.UserInsertDTO;
import br.com.impacta.customers.dto.UserUpdateDTO;
import br.com.impacta.customers.entity.CustomersEntity;
import br.com.impacta.customers.entity.UserEntity;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.impacta.customers.repository.UserRepository;
import br.com.impacta.customers.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService service;

	@MockBean
	private UserRepository repository;

	@Mock
	private BCryptPasswordEncoder passwordEncoder;

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;

	private String operatorEmail;
	private String operatorPassword;
	private String token;
	private String URL_USERS = "/users";

	@BeforeEach
	void setUp() throws Exception {

		operatorEmail = "maria@gmail.com";
		operatorPassword = "123456";
		token = obtainAccessToken(operatorEmail, operatorPassword);
	}
	
	@Test
	public void get_allUsers_returnsOkWithListOfUsers() throws Exception {

		List<UserEntity> list = List.of(
				UserEntityTestFactory.createUser(1L, "Teste", "teste1", "teste@gmail.com"),
				UserEntityTestFactory.createUser(2L, "Teste", "teste2", "teste2@gmail.com"));
		Page<UserEntity> pages = new PageImpl<UserEntity>(list);
		
		PageRequest pageRequest = PageRequest.of(0, 12, Direction.valueOf("ASC"), "firstName");

		when(service.findAllPaged(pageRequest)).thenReturn(pages);
		
		ResultActions result = mockMvc.perform(get(URL_USERS)
				.header("Authorization", "Bearer " + token)
				.contentType(APPLICATION_JSON_UTF8));
		
		result.andExpect(status().isOk());

	}

	@Test
	public void get_findUserByIdSuccess() throws Exception {
		Long clientId = 100L;

		Optional<UserEntity> userEntity = Optional.ofNullable(UserEntityTestFactory.createUser(1L, "Teste", "teste1", "teste@gmail.com"));

		when(repository.findById(anyLong())).thenReturn(userEntity);

		mockMvc.perform(get(URL_USERS + "/{id}", clientId)
				.header("Authorization", "Bearer " + token)
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void get_findUserByIdNotFound() throws Exception {
		Long userId = 10L;

		mockMvc.perform(get(URL_USERS + "/{id}", userId)
				.header("Authorization", "Bearer " + token)
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().isNotFound());
	}

	@Test
	public void post_saveUsersAndReturnTheResource201() throws Exception {
		String password = "34rfyu";

		UserInsertDTO dto = UserEntityTestFactory.createUserInsertDto("Test","teste","teste@gmail.com", "123456");
		UserEntity entitySave = UserEntityTestFactory.createUser(null, "Teste", "teste1", "teste@gmail.com", "123456");
		UserEntity entityReturn = UserEntityTestFactory.createUser(1L, "Teste", "teste1", "teste@gmail.com", password);

		objectMapper = new ObjectMapper();

		when(passwordEncoder.encode(any())).thenReturn(password);
		when(repository.save(entitySave)).thenReturn(entityReturn);

		ResultActions result = mockMvc.perform(post(URL_USERS)
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(dto))
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8));

		result.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void put_updatesAndReturnsUpdatedObjWith204() throws Exception {

		UserEntity userEntity = UserEntityTestFactory.createUser(1l, "Teste", "teste1", "teste@gmail.com", "123456");
		UserUpdateDTO dto = UserEntityTestFactory.createUserUpdateDto(userEntity.getId(), userEntity.getLastName(), userEntity.getLastName(), userEntity.getEmail(), userEntity.getPassword());

		when(repository.getOne(anyLong())).thenReturn(userEntity);
		when(service.update(anyLong(), userEntity)).thenReturn(userEntity);

		ResultActions result = mockMvc.perform(put(URL_USERS + "/{id}", userEntity.getId())
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(dto))
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8));


		result.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void updatesAndReturnNotFound() throws Exception {

		UserEntity userEntity = UserEntityTestFactory.createUser(1l, "Teste", "teste1", "teste@gmail.com", "123456");
		UserUpdateDTO dto = UserEntityTestFactory.createUserUpdateDto(userEntity.getId(), userEntity.getLastName(), userEntity.getLastName(), userEntity.getEmail(), userEntity.getPassword());

		when(repository.getOne(anyLong())).thenReturn(null);

		ResultActions result = mockMvc.perform(put(URL_USERS + "/{id}", userEntity.getId())
				.header("Authorization", "Bearer " + token)
				.content(objectMapper.writeValueAsString(dto))
				.contentType(APPLICATION_JSON_UTF8)
				.accept(APPLICATION_JSON_UTF8));


		result.andExpect(status().isNotFound());

	}

	@Test
	public void deleteUser() throws Exception {
		Long clientId = 100L;
		UserEntity userEntity = UserEntityTestFactory.createUser(clientId, "Teste", "teste1", "teste@gmail.com", "123456");

		when(repository.getOne(clientId)).thenReturn(userEntity);
		doNothing().when(repository).deleteById(clientId);

		ResultActions result = mockMvc.perform(
				delete(URL_USERS + "/{id}", clientId)
						.header("Authorization", "Bearer " + token));

		result.andExpect(status().isNoContent());
	}

	@Test
	public void deleteNotFoundUser() throws Exception {
		Long clientId = 100L;
		when(repository.getOne(clientId)).thenReturn(null);

		ResultActions result = mockMvc.perform(
				delete(URL_USERS + "/{id}", clientId)
						.header("Authorization", "Bearer " + token));

		result.andExpect(status().isNotFound());
	}

	private String obtainAccessToken(String email, String password) throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", clientId);
		params.add("username", email);
		params.add("password", password);

		when(repository.findByEmail(email)).thenReturn(new UserEntity(1L, "Teste", "teste1", "maria@gmail.com", "$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG"));

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
