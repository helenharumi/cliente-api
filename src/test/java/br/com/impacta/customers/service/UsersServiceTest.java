package br.com.impacta.customers.service;

import br.com.impacta.customers.controller.factory.UserEntityTestFactory;
import br.com.impacta.customers.entity.UserEntity;
import br.com.impacta.customers.exceptions.ObjectNotFoundException;
import br.com.impacta.customers.exceptions.ResourceExistsException;
import br.com.impacta.customers.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void findAllPaged(){
        UserEntity user1 = UserEntityTestFactory.createUser(1l,"Teste", "Teste", "teste@gmail.com");
        UserEntity user2 = UserEntityTestFactory.createUser(2L,"Teste", "Teste", "teste@gmail.com");

        Page<UserEntity> pages = new PageImpl<>(List.of(user1, user2));

        PageRequest pageRequest = PageRequest.of(0, 12, Sort.Direction.valueOf("ASC"), "name");

        when(repository.findAll(pageRequest)).thenReturn(pages);

        Page<UserEntity> entitiesPages =  service.findAllPaged(pageRequest);

        assertEquals(entitiesPages.getTotalPages(), 1);
        assertEquals(entitiesPages.getTotalElements(), 2L);

    }

    @Test
    void findId(){
        Long id = 1L;
        Optional<UserEntity> user = Optional.of(UserEntityTestFactory.createUser(1l, "Teste", "Teste", "teste@gmail.com"));

        when(repository.findById(id)).thenReturn(user);

        Optional<UserEntity> userEntityOptional = Optional.ofNullable(service.findById(id));

        assertEquals(userEntityOptional.isPresent(), true);
    }

    @Test
    void findIdNotPresent(){
        Long nonExistingId = 1000L;
        assertThrows(ObjectNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        verify(repository, times(1)).findById(nonExistingId);
    }

    @Test
    void insertSuccess() {
        String password = "34rfyu";
        UserEntity userSave = UserEntityTestFactory.createUser(null,"Teste", "Teste", "teste@gmail.com", "teste");
        UserEntity userReturn = UserEntityTestFactory.createUser(1L,"Teste", "Teste", "teste@gmail.com", password);

        when(repository.save(userSave)).thenReturn(userReturn);

        when(passwordEncoder.encode(userSave.getPassword())).thenReturn(password);

        UserEntity userEntity = service.insert(userSave);

        assertEquals(userEntity.getEmail(), userSave.getEmail());
        assertEquals(userEntity.getPassword(), password);
    }

    @Test
    void insertUserExist() {
        String emailExist = "teste@gmail.com";

        UserEntity user = UserEntityTestFactory.createUser(1L,"Teste", "Teste", "teste@gmail.com");
        UserEntity userExists = user;

        assertThrows(ResourceExistsException.class, () -> {
            when(repository.findByEmail(emailExist)).thenReturn(userExists);
            when(service.insert(user)).thenReturn(userExists);
        });
        verify(repository, Mockito.times(1)).findByEmail(emailExist);

    }

    @Test
    void update() {
        Long id = 1L;

        UserEntity userUpdate = UserEntityTestFactory.createUser(1L,"Teste", "Teste", "teste@gmail.com");
        UserEntity userReturn = UserEntityTestFactory.createUser(1L,"Teste", "Teste", "teste@gmail.com");

        when(repository.getOne(id)).thenReturn(userReturn);
        when(repository.save(userUpdate)).thenReturn(userReturn);

        UserEntity userEntity = service.update(id, userUpdate);

        assertEquals(userEntity.getEmail(), userReturn.getEmail());
        assertEquals(userEntity.getUsername(), userReturn.getUsername());
    }



    @Test
    void updateNotFound() {
        Long nonExistingId = 1000L;
        assertThrows(ObjectNotFoundException.class, () -> {
            service.update(nonExistingId, UserEntityTestFactory.createUser(1L,"Teste", "Teste", "teste@gmail.com"));
        });

        verify(repository, times(1)).getOne(nonExistingId);
    }

    @Test
    public void deleteNotFoundThrow() {
        Long nonExistingId = 1l;
        assertThrows(ObjectNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        verify(repository, Mockito.times(1)).getOne(nonExistingId);
    }

    @Test
    public void deleteNotThrow() {
        Long existingId = 1l;
        assertDoesNotThrow(() -> {
            when(repository.getOne(1l)).thenReturn(UserEntityTestFactory.createUser(1L,"Teste", "Teste", "teste@gmail.com"));
            service.delete(existingId);
        });

        verify(repository, Mockito.times(1)).deleteById(existingId);
    }
}
