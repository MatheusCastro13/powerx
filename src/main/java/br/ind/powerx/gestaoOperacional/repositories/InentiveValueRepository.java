package br.ind.powerx.gestaoOperacional.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.IncentiveValue;

@Repository
public interface InentiveValueRepository extends JpaRepository<IncentiveValue, Long>{
	List<IncentiveValue> findAllByProductId(Long id);

}
