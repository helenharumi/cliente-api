package br.com.impacta.clientes.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.hibernate.ObjectNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.impacta.clientes.dto.ClienteDTO;
import br.com.impacta.clientes.dto.ClienteInsertDTO;
import br.com.impacta.clientes.entity.ClienteEntity;
import br.com.impacta.clientes.service.ClienteService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "/v1/cliente")
public class ClienteController {

	@Autowired
	private ClienteService service;
	
	@Autowired
    private ModelMapper modelMapper;

	@ApiOperation(value = "Insert a new object into the database")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@PostMapping
	public ResponseEntity<ClienteDTO> save(@Valid @RequestBody ClienteInsertDTO clienteDTO) {
		
		if (clienteDTO.getDataNascimento() == null || clienteDTO.getNome() == null  || clienteDTO.getNome().isEmpty())
			return ResponseEntity.badRequest().build();

		ClienteEntity obj = service.save(fromDTO(clienteDTO));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}

	@ApiOperation(value = "Return an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 404, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@GetMapping(value = "/{id}")
	public ResponseEntity<ClienteDTO> find(@PathVariable Long id) {
		ClienteEntity obj = null;
		try {
			obj = service.findById(id);
			return ResponseEntity.ok().body(toClienteDTO(obj));
		} catch (ObjectNotFoundException e) {
			return ResponseEntity.notFound().build();
		}

	}

	@ApiOperation(value = "Return an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@GetMapping(value = "/findByName/{nome}")
	public ResponseEntity<ClienteDTO> findByNome(@PathVariable String nome) {

		ClienteEntity obj;
		try {
			obj = service.findByNome(nome);
			return ResponseEntity.ok().body(toClienteDTO(obj));
		} catch (ObjectNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@ApiOperation(value = "Returns all objects")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ClienteDTO>> findAll() {

		List<ClienteEntity> list = service.findAll();
		List<ClienteDTO> listDto = modelMapper.map(list, new TypeToken<List<ClienteDTO>>() {}.getType());
		
		return ResponseEntity.ok().body(listDto);
	}

	@ApiOperation(value = "Update an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 404, message = "This resource not found "),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ClienteDTO> update(@Valid @RequestBody ClienteInsertDTO objDto, @PathVariable Long id) {
		ClienteEntity obj = fromDTO(objDto);
		obj.setId(id);
		ClienteEntity body = service.update(obj);
		return ResponseEntity.ok(toClienteDTO(body));
	}

	@ApiOperation(value = "Delete an object")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 401, message = "You do not have permission to access this resource ((Unauthorized))"),
			@ApiResponse(code = 403, message = "You do not have permission to access this resource"),
			@ApiResponse(code = 500, message = "an exception was thrown"), })
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		try {
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (ObjectNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	public ClienteDTO toClienteDTO(ClienteEntity obj) {
		return new ClienteDTO(obj.getId(), obj.getNome(), obj.getDataNascimento());
	}

	public ClienteEntity fromDTO(ClienteInsertDTO objDto) {
		return modelMapper.map(objDto, ClienteEntity.class);
	}
	
	
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}
}
