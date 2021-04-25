package br.com.impacta.customers.service;

import br.com.impacta.customers.entity.CustomersEntity;
import br.com.impacta.customers.exceptions.ObjectNotFoundException;
import br.com.impacta.customers.repository.CustomersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CustomersServiceTest {

    @InjectMocks
    private CustomersService service;

    @Mock
    private CustomersRepository repository;

    @Test
    void save() {
        CustomersEntity customers = new CustomersEntity();
        customers.setName("João");

        CustomersEntity customersEntity = new CustomersEntity();
        customersEntity.setName("João");
        customers.setId(1L);

        Mockito.when(repository.save(customers)).thenReturn(customersEntity);

        CustomersEntity response = service.save(customers);

        assertEquals("João", response.getName());
    }

    @Test
    void findByIdSucess() {
        CustomersEntity customersEntity = new CustomersEntity();
        customersEntity.setName("João");
        customersEntity.setId(1L);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(customersEntity));

        CustomersEntity response = service.findById(1L);

        assertEquals("João", response.getName());
        assertEquals(1L, response.getId());
    }

    @Test
    void findByIdThrowsException(){
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void update() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findByName() {
    }
}