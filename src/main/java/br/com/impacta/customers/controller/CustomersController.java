package br.com.impacta.customers.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
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

import br.com.impacta.customers.dto.CustomersDTO;
import br.com.impacta.customers.dto.CustomersInsertDTO;
import br.com.impacta.customers.entity.CustomersEntity;
import br.com.impacta.customers.service.CustomersService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/v1/customers")
public class CustomersController {

	@Autowired
	private CustomersService service;

	@Autowired
	private ModelMapper modelMapper;
	
	
	@ApiOperation(value = "Find all users")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 400, message = "the server cannot or will not process the request due to something that was perceived as a client error"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@GetMapping
	public ResponseEntity<Page<CustomersDTO>> findAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction ,
			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy
			) {
		
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		Page<CustomersEntity> list = service.findAllPaged(pageRequest);
		
		Page<CustomersDTO> listDto =  modelMapper.map(list, new TypeToken<Page<CustomersDTO>>() {
		}.getType());
		
		return ResponseEntity.ok().body(listDto);
	}

	@ApiOperation(value = "Insert a new object into the database")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 400, message = "the server cannot or will not process the request due to something that was perceived as a client error"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@PostMapping
	public ResponseEntity<CustomersDTO> save(@Valid @RequestBody CustomersInsertDTO customersDTO) {
		CustomersEntity obj = service.save(convertFromCustomersDTOtoCustomersDTO(customersDTO));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}

	@ApiOperation(value = "Return an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 400, message = "the server cannot or will not process the request due to something that was perceived as a client error"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 404, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@GetMapping(value = "/{id}")
	public ResponseEntity<CustomersDTO> find(@PathVariable @Size(min = 1)  Long id) {
		CustomersEntity obj = null;
		obj = service.findById(id);
		return ResponseEntity.ok().body(convertToCustomersDTO(obj));
	}

	@ApiOperation(value = "Return an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 400, message = "the server cannot or will not process the request due to something that was perceived as a client error"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown") })
	@GetMapping(value = "/findByName/{name}")
	public ResponseEntity<List<CustomersDTO>> findByName(@PathVariable @NotBlank String name) {

		List<CustomersEntity> list = service.findByName(name);
		
		TypeToken<List<CustomersEntity>> typeToken = new TypeToken<>() {};
		
		List<CustomersDTO> listDto = modelMapper.map(list, typeToken.getType() );
		
		return ResponseEntity.ok().body(listDto);
	}

	@ApiOperation(value = "Update an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 400, message = "the server cannot or will not process the request due to something that was perceived as a client error"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 404, message = "This resource not found "),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CustomersDTO> update(@Valid @RequestBody CustomersInsertDTO objDto, @PathVariable Long id) {
		CustomersEntity obj = convertFromCustomersDTOtoCustomersDTO(objDto);
		obj.setId(id);
		CustomersEntity body = service.update(obj);
		return ResponseEntity.ok(convertToCustomersDTO(body));
	}

	@ApiOperation(value = "Delete an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
			service.deleteById(id);
			return ResponseEntity.noContent().build();
	}

	public CustomersDTO convertToCustomersDTO(CustomersEntity obj) {
		return modelMapper.map(obj, CustomersDTO.class);
	}

	public CustomersEntity convertFromCustomersDTOtoCustomersDTO(CustomersInsertDTO objDto) {
		return modelMapper.map(objDto, CustomersEntity.class);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
