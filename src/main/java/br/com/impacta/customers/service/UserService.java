package br.com.impacta.customers.service;

import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.impacta.customers.dto.RoleDTO;
import br.com.impacta.customers.dto.UserDTO;
import br.com.impacta.customers.dto.UserInsertDTO;
import br.com.impacta.customers.dto.UserUpdateDTO;
import br.com.impacta.customers.entity.RoleEntity;
import br.com.impacta.customers.entity.UserEntity;
import br.com.impacta.customers.exceptions.DataBaseException;
import br.com.impacta.customers.exceptions.ObjectNotFoundException;
import br.com.impacta.customers.repository.RoleRepository;
import br.com.impacta.customers.repository.UserRepository;
import org.springframework.web.client.ResourceAccessException;

@Service
public class UserService implements UserDetailsService {

	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;

	@Transactional(readOnly = true)
	public Page<UserEntity> findAllPaged(PageRequest page) {
		return repository.findAll(page);
	}

	@Transactional(readOnly = true)
	public UserEntity findById(Long id) {
		Optional<UserEntity> obj = repository.findById(id);
		UserEntity entity = obj.orElseThrow(() -> new ObjectNotFoundException("Entity not found"));
		return entity;
	}

	@Transactional
	public UserEntity insert(UserEntity userEntity) {
		userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
		return repository.save(userEntity);
	}

	@Transactional
	public UserEntity update(Long id, UserEntity entityUpdate) {

		UserEntity entity = repository.getOne(id);
		if(entity == null){
			throw new ObjectNotFoundException("Id not found " + id);
		}
		if(entityUpdate.getPassword() != null){
			entityUpdate.setPassword(passwordEncoder.encode(entityUpdate.getPassword()));
		}

		entityUpdate.setId(id);
		return repository.save(entityUpdate);

	}

	public void delete(Long id) {

		UserEntity entity = repository.getOne(id);
		if(entity == null){
			throw new ObjectNotFoundException("Id not found " + id);
		}
		repository.deleteById(id);

	}

	public UserEntity copyDtoToEntity(UserDTO dto, UserEntity entity) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());

		entity.getRoles().clear();

		for (RoleDTO roleDto : dto.getRoles()) {
			Optional<RoleEntity> optional = roleRepository.findById(roleDto.getId());
			RoleEntity role = optional.orElseThrow(() -> new ObjectNotFoundException("Entity not found"));
			entity.getRoles().add(role);
		}

		return entity;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity user = repository.findByEmail(email);
		if(user == null){
			logger.error("User not found", email);
			throw new UsernameNotFoundException("Email not found");
		}
		return user;
	}
}
