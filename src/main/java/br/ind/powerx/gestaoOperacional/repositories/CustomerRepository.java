package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

	List<Customer> findAllByActiveTrue();

	List<Customer> findAllByUserIdNull();

	List<Customer> findAllByGroupIdNull();

	List<Customer> findByUserIdIn(List<Long> users);

	List<Customer> findByGroupIdIn(List<Long> groups);

	List<Customer> findByUserIdInAndGroupIdIn(List<Long> users, List<Long> groups);

}
