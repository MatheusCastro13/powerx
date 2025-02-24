package br.ind.powerx.gestaoOperacional.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Function;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Long> {

	Function findByName(String functionName);

}
