package br.ind.powerx.gestaoOperacional.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Group;

@Repository
public interface PartnerGroupRepository extends JpaRepository<Group, Long>{

}