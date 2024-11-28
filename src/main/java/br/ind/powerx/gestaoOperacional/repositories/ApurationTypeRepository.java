package br.ind.powerx.gestaoOperacional.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.ApurationType;

@Repository
public interface ApurationTypeRepository extends JpaRepository<ApurationType, Long> {

}
