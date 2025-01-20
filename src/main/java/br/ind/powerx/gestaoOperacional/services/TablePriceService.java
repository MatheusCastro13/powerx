package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.TablePrice;
import br.ind.powerx.gestaoOperacional.model.dtos.TablePriceSaveDTO;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.ProductRepository;
import br.ind.powerx.gestaoOperacional.repositories.TablePriceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class TablePriceService {

	private final TablePriceRepository tableRepository;
	
	private final CustomerRepository customerRepository;

	private final ProductRepository productRepository;
	
	@Autowired
	public TablePriceService(TablePriceRepository tableRepository, CustomerRepository customerRepository,
			ProductRepository productRepository) {
		this.tableRepository = tableRepository;
		this.customerRepository = customerRepository;
		this.productRepository = productRepository;
	}

	public List<TablePrice> findAll() {
		return tableRepository.findAll();
	}

	public Page<TablePrice> findAll(Pageable pageable) {
		return tableRepository.findAll(pageable);
	}
	
	@Transactional
	public void save(TablePriceSaveDTO tableDTO) {
		TablePrice table = new TablePrice();
		Customer customer = findCustomerById(tableDTO.customer());
		Product product = findProductById(tableDTO.product());
		table.setCustomer(customer);
		table.setProduct(product);
		table.setPrice(tableDTO.price());
		tableRepository.save(table);
		
	}
	
	@Transactional
	public void update(Long id, TablePriceSaveDTO tableDTO) {
	    TablePrice table = tableRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Tabela de Preço não encontrada"));

	    Customer customer = findCustomerById(tableDTO.customer());
	    Product product = findProductById(tableDTO.product());

	    if (!table.getCustomer().equals(customer)) {
	        table.setCustomer(customer);
	    }

	    if (!table.getProduct().equals(product)) {
	        table.setProduct(product);
	    }

	    table.setPrice(tableDTO.price());

	    tableRepository.save(table);
	}

	
	private Customer findCustomerById(Long customerId) {
		return customerRepository.findById(customerId)
					.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
	}
	
	private Product findProductById(Long productId) {
		return productRepository.findById(productId)
					.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
	}
	
}














