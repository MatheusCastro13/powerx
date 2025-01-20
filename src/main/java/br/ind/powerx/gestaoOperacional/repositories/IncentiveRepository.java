package br.ind.powerx.gestaoOperacional.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.enums.State;

@Repository
public interface IncentiveRepository extends JpaRepository<Incentive, Long> {

	@Query("SELECT DISTINCT i.saleDocumentNumber FROM Incentive i")
	List<Integer> findDistinctDocumentNumbers();
	
	List<Incentive> findBySaleDocumentNumber(Integer saleDocumentNumber);
	
	List<Incentive> findByReferenceDateBetweenAndCustomerAndApurationType(
            LocalDate dataInicial, LocalDate dataFinal, Customer customer, ApurationType apurationType);


	List<Incentive> findByReferenceDateBetweenAndApurationTypeAndState(LocalDate dataInicial, LocalDate dataFinal,
			ApurationType apurationType, State state);

	List<Incentive> findByUser(User user);

	@Query("SELECT DISTINCT i.saleDocumentNumber FROM Incentive i WHERE i.user.id = :userId")
	List<Integer> findDistinctDocumentNumbersByUserId(@Param("userId") Long userId);

	Page<Incentive> findByUser(User user, Pageable pageable);

}
