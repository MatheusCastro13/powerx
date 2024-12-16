package br.ind.powerx.gestaoOperacional.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Sale;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

	@Query("SELECT MAX(s.ordem) FROM Sale s WHERE s.customer.id = :customerId")
    Integer findMaxOrderForCustomer(@Param("customerId") Long customerId);
}
