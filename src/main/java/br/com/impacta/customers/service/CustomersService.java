package br.com.impacta.customers.service;

import br.com.impacta.customers.entity.CustomersEntity;
import br.com.impacta.customers.exceptions.ObjectNotFoundException;
import br.com.impacta.customers.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomersService {

	@Autowired
	private CustomersRepository repository;

	
	@Transactional(readOnly = true)
	public Page<CustomersEntity> findAllPaged(PageRequest page) {
		return repository.findAll(page);
	}
	
	public CustomersEntity save(CustomersEntity customer) {
		return repository.save(customer);
	}

	public CustomersEntity findById(Long id) {
		Optional<CustomersEntity> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"object not found! Id: " + id + ", Type: " + CustomersEntity.class.getName(), null));
	}

	public CustomersEntity update(CustomersEntity obj) {
		CustomersEntity newObj = repository.findById(obj.getId()).orElseThrow(() -> new ObjectNotFoundException(
				"object not found! Id: " + obj.getId() + ", Type: " + CustomersEntity.class.getName(), null));
		updateData(newObj, obj);
		return repository.save(newObj);
	}

	private void updateData(CustomersEntity newObj, CustomersEntity obj) {
		newObj.setName(obj.getName());
		newObj.setBirthDate(obj.getBirthDate());
	}

	public void deleteById(Long id) {
		findById(id);
		try {
			repository.deleteById(id);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to delete the client");
		}
	}

	public List<CustomersEntity> findByName(String name) {

		if (name == null || name.isEmpty())
			throw new IllegalArgumentException("filter cannot be empty");

		List<CustomersEntity> obj = repository.findByNameIgnoreCase(name);
		
		if (obj == null || obj.isEmpty())
			throw new ObjectNotFoundException("object not found by name"); 
		return obj;
	}

}
