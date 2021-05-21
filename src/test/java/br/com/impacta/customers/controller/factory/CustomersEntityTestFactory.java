package br.com.impacta.customers.controller.factory;

import br.com.impacta.customers.dto.CustomersDTO;
import br.com.impacta.customers.dto.CustomersInsertDTO;
import br.com.impacta.customers.entity.CustomersEntity;

import java.time.LocalDateTime;

public class CustomersEntityTestFactory {

    public static CustomersEntity createCustomer(Long id, String name){
        return new CustomersEntity(1L, "Maria", LocalDateTime.now());
    }

    public static CustomersInsertDTO createCustomerInsertDTO(String client) {
        CustomersInsertDTO customersInsertDTO = new CustomersInsertDTO();
        customersInsertDTO.setName(client);
        customersInsertDTO.setBirthDate(LocalDateTime.of(1992, 07, 20, 23, 59));
        return customersInsertDTO;
    }

    public static CustomersDTO createCustomerDTO(Long id, String client) {
        CustomersDTO customersDTO = new CustomersDTO();
        customersDTO.setName(client);
        customersDTO.setBirthDate(LocalDateTime.of(1992, 07, 20, 23, 59));
        return customersDTO;
    }

}
