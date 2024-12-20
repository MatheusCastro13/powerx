package br.ind.powerx.gestaoOperacional.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.CompactIncentive;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductSaleDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.SalesDTO;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.EmployeeRepository;
import br.ind.powerx.gestaoOperacional.repositories.ProductRepository;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SaleService {

	@Autowired
	private SaleRepository saleRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private EmployeeRepository empRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CalculeIncentiveService calculeIncentiveService;
	
	public List<CompactIncentive> saveSales(List<SalesDTO> salesDTO) {
		
		List<Sale> sales = new ArrayList<>();
		for(SalesDTO sale : salesDTO) {			
			if(sale.customerId() == null) {
				throw new NullPointerException("O id do cliente não pode ser nulo");
			}
			Long customerId = sale.customerId();
			Customer customer = customerRepository.findById(customerId)
					.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
			
			
			if(sale.employeeId() == null) {
				throw new NullPointerException("O id do vendedor não pode ser nulo");
			}
			Long employeeId = sale.employeeId();
			Employee emp = empRepository.findById(employeeId)
					.orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado"));
			
			
			Map<Product, Integer> productAndQuantity = new HashMap<>();
			for(ProductSaleDTO product : sale.products()) {
				if(product.productId() == null) {
					throw new NullPointerException("O id do produto não pode ser nulo");
				}
				
				if(product.quantity() == null) {
					throw new NullPointerException("A quantidade não pode ser nulo");
				}
				
				Product productFinded = productRepository.findById(product.productId())
						.orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
				
				productAndQuantity.put(productFinded, product.quantity());
				
				
				Sale saleToSave = new Sale(customer, emp, productFinded, product.quantity());
				
				Integer maxOrdem = saleRepository.findMaxOrderForCustomer(customer.getId());

		        int novaOrdem = (maxOrdem != null ? maxOrdem : 0) + 1;

		        saleToSave.setOrdem(novaOrdem);
		        
				sales.add(saleToSave);
				
			}
		}
		
		
		List<CompactIncentive> incentives = calculeIncentiveService.calculateIncentives(sales);
		
		return incentives;
	}
	
}























