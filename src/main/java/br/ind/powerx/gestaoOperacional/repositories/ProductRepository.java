package br.ind.powerx.gestaoOperacional.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	Product findByProductCode(String productCode);


}
