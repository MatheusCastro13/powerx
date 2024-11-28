package br.ind.powerx.gestaoOperacional.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Incentive;

@Repository
public interface IncentiveRepository extends JpaRepository<Incentive, Long> {

}