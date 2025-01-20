package br.ind.powerx.gestaoOperacional.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.TablePrice;

@Repository
public interface TablePriceRepository extends JpaRepository<TablePrice, Long>{

}
