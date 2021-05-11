package br.com.impacta.customers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.impacta.customers.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

}
