package br.com.impacta.clientes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.impacta.clientes.entity.ClienteEntity;
import br.com.impacta.clientes.exceptions.ObjectNotFoundException;
import br.com.impacta.clientes.repository.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;

	public ClienteEntity save(ClienteEntity cliente) {
		return repository.save(cliente);
	}

	public ClienteEntity findById(Long id) {
		Optional<ClienteEntity> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"object not found! Id: " + id + ", Type: " + ClienteEntity.class.getName(), null));
	}

	public ClienteEntity update(ClienteEntity obj) {
		ClienteEntity newObj = repository.findById(obj.getId()).orElseThrow(() -> new ObjectNotFoundException(
				"object not found! Id: " + obj.getId() + ", Type: " + ClienteEntity.class.getName(), null));
		updateData(newObj, obj);
		return repository.save(newObj);
	}

	private void updateData(ClienteEntity newObj, ClienteEntity obj) {
		newObj.setNome(obj.getNome());
		newObj.setDataNascimento(obj.getDataNascimento());
	}

	public void deleteById(Long id) {
		findById(id);
		try {
			repository.deleteById(id);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to delete the client");
		}
	}

	public List<ClienteEntity> findAll() {
		return repository.findAll();
	}

	public ClienteEntity findByNome(String nome) {

		if (nome == null || nome.isEmpty())
			throw new IllegalArgumentException("filter cannot be empty");

		Optional<ClienteEntity> obj = repository.findByNome(nome);

		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"object not found! Nome: " + nome + ", Type: " + ClienteEntity.class.getName(), null));
	}

}
