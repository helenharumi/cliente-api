package br.com.impacta.customers.controller.factory;

import br.com.impacta.customers.entity.UserEntity;

public class UserEntityTestFactory {

    public static UserEntity createUser(Long id, String firstName, String lastName, String email, String password ){
        return new UserEntity(id, firstName, lastName, email, password);
    }

}
