package br.com.impacta.clientes.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.impacta.clientes.dto.ClienteDTO;
import br.com.impacta.clientes.entity.ClienteEntity;
import br.com.impacta.clientes.repository.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	public ClienteDTO incluir(ClienteDTO clienteDTO) {

		return toClienteDTO(clienteRepository.save(fromDTO(clienteDTO)));
	}

	public ClienteDTO toClienteDTO(ClienteEntity obj) {
		return new ClienteDTO(obj.getId(), obj.getNome(), obj.getDataNascimento());
	}

	public ClienteDTO findById(Long id) {

		Optional<ClienteEntity> obj = clienteRepository.findById(id);

		return toClienteDTO(obj.orElseThrow(() -> new ObjectNotFoundException(
				"object not found! Id: " + id + ", Type: " + ClienteEntity.class.getName(), null)));
	}

	public ClienteEntity fromDTO(ClienteDTO objDto) {

		ClienteEntity clienteEntity = new ClienteEntity();
		clienteEntity.setNome(objDto.getNome());
		clienteEntity.setDataNascimento(objDto.getDataNascimento());
		return clienteEntity;
	}

	public ClienteDTO update(ClienteDTO obj) {
		ClienteEntity newObj = clienteRepository.findById(obj.getId()).orElseGet(null);
		updateData(newObj, obj);
		return toClienteDTO(clienteRepository.save(newObj));
	}

	private void updateData(ClienteEntity newObj, ClienteDTO obj) {
		newObj.setNome(obj.getNome());
		newObj.setDataNascimento(obj.getDataNascimento());
	}

	public void deleteById(Long id) {
		findById(id);
		try {
			clienteRepository.deleteById(id);
		} catch (Exception e) {
			throw new IllegalArgumentException("You cannot delete a client");
		}
	}

	public List<ClienteEntity> findAll() {
		return clienteRepository.findAll();
	}

	public ClienteDTO findByNome(String nome) {

		Optional<ClienteEntity> obj = clienteRepository.findByNome(nome);

		return toClienteDTO(obj.orElseThrow(() -> new ObjectNotFoundException(
				"object not found! Nome: " + nome + ", Type: " + ClienteEntity.class.getName(), null)));

	}

}
