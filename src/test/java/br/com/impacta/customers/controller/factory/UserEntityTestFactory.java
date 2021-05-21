package br.com.impacta.customers.controller.factory;

import br.com.impacta.customers.dto.UserDTO;
import br.com.impacta.customers.dto.UserInsertDTO;
import br.com.impacta.customers.dto.UserUpdateDTO;
import br.com.impacta.customers.entity.UserEntity;

public class UserEntityTestFactory {

    public static UserEntity createUser(Long id, String firstName, String lastName, String email){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setEmail(email);
        return userEntity;
    }

    public static UserEntity createUser(Long id, String firstName, String lastName, String email, String password){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        return userEntity;
    }

    public static UserInsertDTO createUserInsertDto(String firstName, String lastName, String email,String password){
        UserInsertDTO userDto = new UserInsertDTO();
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        userDto.setPassword(password);
        return userDto;
    }

    public static UserUpdateDTO createUserUpdateDto(Long id, String lastName, String firstName, String email, String password) {
        UserUpdateDTO userDto = new UserUpdateDTO();
        userDto.setId(id);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        userDto.setPassword(password);
        return userDto;

    }
}
