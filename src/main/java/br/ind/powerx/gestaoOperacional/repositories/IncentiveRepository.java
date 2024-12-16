package br.ind.powerx.gestaoOperacional.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Incentive;

@Repository
public interface IncentiveRepository extends JpaRepository<Incentive, Long> {

	 @Query("SELECT MAX(i.ordem) FROM Incentive i WHERE i.customer.id = :customerId")
	    Integer findMaxOrderForCustomer(@Param("customerId") Long customerId);
}
