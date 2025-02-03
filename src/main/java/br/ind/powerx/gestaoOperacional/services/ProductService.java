package br.ind.powerx.gestaoOperacional.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.IncentiveValue;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.dtos.AddIncentiveValueDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.IncentiveValueDTO;
import br.ind.powerx.gestaoOperacional.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private IncentiveValueService valueService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private FunctionService functionService;
	
	public void save(Product product) {
		if(product.getUnysoftCode() != null) {
			if(product.getUnysoftCode().isBlank() || product.getUnysoftCode().isEmpty() || product.getUnysoftCode().equals("")) {
				product.setUnysoftCode(null);
				System.out.println("QUERO GOZAR------------------------------------------------");
			}
		}
		productRepository.save(product);
	}
	
	public List<Product> findAll(){
		return productRepository.findAll();
	}

	@Transactional
	public void addIncentiveValue(AddIncentiveValueDTO incentiveDTO) {
		Product product = productRepository.findById(incentiveDTO.productId())
				.orElseThrow(()-> new EntityNotFoundException("Produto não encontrado"));
		
		List<IncentiveValueDTO> incentiveValueDTO = incentiveDTO.incentives();
		List<IncentiveValue> incentiveValues = new ArrayList<>();
		
		for(int i = 0; i < incentiveValueDTO.size(); i++) {
			
			Long customerId = incentiveValueDTO.get(i).customerId();
			Customer customer = customerService.findById(customerId)
					.orElseThrow(()-> new EntityNotFoundException("Cliente não encontrado"));
			
			Long functionId = incentiveValueDTO.get(i).functionId();
			Function function = functionService.findById(functionId)
					.orElseThrow(()-> new EntityNotFoundException("Função não encontrado"));
			
			BigDecimal ccValue = incentiveValueDTO.get(i).ccValue();
			
			BigDecimal nfsValue = incentiveValueDTO.get(i).nfsValue();
			
			BigDecimal overValue = incentiveValueDTO.get(i).overValue();
			
			IncentiveValue incentiveValue = new IncentiveValue();
			incentiveValue.setCustomer(customer);
			incentiveValue.setFunction(function);
			incentiveValue.setId(null);
			incentiveValue.setProduct(product);
			incentiveValue.setCcValue(ccValue);
			incentiveValue.setCcValue(nfsValue);
			incentiveValue.setOverValue(overValue);

			incentiveValues.add(incentiveValue);
		}
		
		for(IncentiveValue i : incentiveValues) {
			i.setProduct(product);
			valueService.save(i);
			product.addIncentiveValue(i);
		}
		
		productRepository.save(product);
	}

	@Transactional
	public void addIncentiveValue(Long id, List<Long> customers, List<Long> functions, List<Double> ccValues, List<Double> nfsValues, 
			List<Double> overValues) {		
	    Product product = productRepository.findById(id)
	            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        Long customerId = customers.get(0);
        
	    Customer customer = customerService.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

	    List<IncentiveValue> currentValues = valueService.findAllByProductAndCustomer(product.getId(), customer.getId());

	    currentValues.forEach(v -> {
	        product.removeIncentiveValue(v);
	        valueService.delete(v);
	    });


	    for (int i = 0; i < functions.size(); i++) {
	        Long functionId = functions.get(i);
	        Double ccValueD = ccValues.get(i);
	        Double nfsValueD = nfsValues.get(i);
	        Double overValueD = overValues.get(i);

	        Function function = functionService.findById(functionId)
	                .orElseThrow(() -> new EntityNotFoundException("Função não encontrada"));


	        BigDecimal ccValue = new BigDecimal(ccValueD);
	        
	        BigDecimal nfsValue = new BigDecimal(nfsValueD);
	        
	        BigDecimal overValue = new BigDecimal(overValueD);

	        IncentiveValue incentive = new IncentiveValue(null, customer, function, product, ccValue, nfsValue, overValue);
	        valueService.save(incentive);
	        product.addIncentiveValue(incentive);
	    }

	    productRepository.save(product);
	}

	@Transactional
	public void removeIncentiveValue(Long productId, Long incentiveValueId) {
		
		Product product = productRepository.findById(productId)
				.orElseThrow(()-> new EntityNotFoundException("Produto não encontrado"));
		
		IncentiveValue incentive = valueService.findById(incentiveValueId)
				.orElseThrow(()-> new EntityNotFoundException("Incentivo não encontrado"));
		
		List<IncentiveValue> currentValues = product.getIncentiveValues();
		
		if(currentValues.contains(incentive)) {
			product.removeIncentiveValue(incentive);
			valueService.delete(incentive);
		}
		
	}

	public Page<Product> findAll(Pageable pageable) {
		return productRepository.findAll(pageable);
	}

	public Product findById(Long productId) {
		return productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
	}

	

	

}


































