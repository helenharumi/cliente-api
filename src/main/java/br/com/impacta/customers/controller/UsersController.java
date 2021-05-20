package br.com.impacta.customers.controller;

import java.net.URI;

import javax.validation.Valid;

import br.com.impacta.customers.dto.CustomersDTO;
import br.com.impacta.customers.entity.UserEntity;
import org.h2.engine.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.impacta.customers.dto.UserDTO;
import br.com.impacta.customers.dto.UserInsertDTO;
import br.com.impacta.customers.dto.UserUpdateDTO;
import br.com.impacta.customers.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/v1/users")
public class UsersController {

	@Autowired
	private UserService service;

	@Autowired
	private ModelMapper modelMapper;

	@ApiOperation(value = "Find all users")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 400, message = "the server cannot or will not process the request due to something that was perceived as a client error"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@GetMapping
	public ResponseEntity<Page<UserDTO>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction ,
			@RequestParam(value = "orderBy", defaultValue = "firstName") String orderBy
			) {
		
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		Page<UserEntity> list = service.findAllPaged(pageRequest);

		Page<UserDTO> listDto =  modelMapper.map(list, new TypeToken<Page<CustomersDTO>>() {
		}.getType());

		return ResponseEntity.ok().body(listDto);
	}

	@ApiOperation(value = "Return an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 400, message = "the server cannot or will not process the request due to something that was perceived as a client error"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 404, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
		UserEntity entity = service.findById(id);
		UserDTO dto = modelMapper.map(entity, UserDTO.class);
		return ResponseEntity.ok().body(dto);
	}

	@ApiOperation(value = "Insert a new object into the database")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 400, message = "the server cannot or will not process the request due to something that was perceived as a client error"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@PostMapping
	public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto) {
		UserEntity user = service.insert(service.copyDtoToEntity(dto, new UserEntity()));
		UserDTO dtoSave = modelMapper.map(user, UserDTO.class);
		URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{id}").buildAndExpand(dtoSave.getId())
				.toUri();
		return ResponseEntity.created(uri).body(dtoSave);
	}

	@ApiOperation(value = "Update an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 400, message = "the server cannot or will not process the request due to something that was perceived as a client error"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 404, message = "This resource not found "),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
		UserEntity userEntityUpdate = service.copyDtoToEntity(dto, new UserEntity());
		UserEntity userEntitySave =  service.update(id, userEntityUpdate);
		UserDTO dtoSave = modelMapper.map(userEntitySave, UserDTO.class);
		return ResponseEntity.ok().body(dtoSave);
	}

	@ApiOperation(value = "Delete an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
