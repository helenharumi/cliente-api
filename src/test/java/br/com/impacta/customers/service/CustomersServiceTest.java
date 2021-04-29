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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    void updateSucess() {
        CustomersEntity customers = new CustomersEntity();
        customers.setId(1L);
        customers.setName("João");
        customers.setBirthDate(LocalDateTime.of(1990,5,18, 23, 0));

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(customers));
        Mockito.when(repository.save(customers)).thenReturn(customers);

        CustomersEntity response = service.update(customers);

        assertEquals(1L, response.getId());
    }

    @Test
    void updateObjectNotFoundException() {
        CustomersEntity customers = new CustomersEntity();
        customers.setId(1L);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service.update(customers));
    }

    @Test
    void deleteById() {
        CustomersEntity customers = new CustomersEntity();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(customers));

        service.deleteById(1L);

        Mockito.verify(repository,Mockito.times(1)).deleteById(1L);
    }

    @Test
    void deleteByIdIllegal() {
        CustomersEntity customers = new CustomersEntity();
        Mockito.when(repository.findById(null)).thenReturn(Optional.of(customers));

        Mockito.doThrow(new IllegalArgumentException()).when(repository).deleteById(null);

        assertThrows(IllegalArgumentException.class, () ->  service.deleteById(null));
    }

    @Test
    void findAll() {
        CustomersEntity customersEntity = new CustomersEntity();
        customersEntity.setName("João");
        customersEntity.setId(1L);
        customersEntity.setBirthDate(LocalDateTime.of(1990,5,18, 23, 0));

        List<CustomersEntity> customers = new ArrayList<>();
        customers.add(customersEntity);

        Mockito.when(repository.findAll()).thenReturn(customers);

        List<CustomersEntity> response = service.findAll();

        assertEquals(response.get(0).getName(), "João");
        assertEquals(response.get(0).getId(), 1L);
        assertEquals(response.get(0).getBirthDate(), LocalDateTime.of(1990, 5, 18, 23, 0));
    }

    @Test
    void findByNameShouldThrowExceptionWhenEmpty() {

       assertThrows(IllegalArgumentException.class, () -> service.findByName(""));
    }

    @Test
    void findByNameSucess() {
        List<CustomersEntity> customers = new ArrayList<>();
        CustomersEntity customersEntity = new CustomersEntity();
        customersEntity.setName("João");

        customers.add(customersEntity);

        Mockito.when(repository.findByNameIgnoreCase("João")).thenReturn(customers);
        List<CustomersEntity> response = service.findByName("João");

        assertEquals(response.get(0).getName(), "João");
    }
}