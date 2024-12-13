package br.ind.powerx.gestaoOperacional.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.IncentiveValue;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveValueRepository;

@Service
public class CalculeIncentiveService {
	
	@Autowired
	private FunctionRepository funcRepo;
	
	@Autowired
	private IncentiveValueRepository incentiveValueRepository;
	
	@Autowired
	private ApurationTypeRepository apurationTypeRepository;
	
	@Autowired
    private AuthenticationService authenticationService;

	public List<Incentive> calculeIncentive(List<Sale> sales) {
		User user = authenticationService.getUserAuthenticated();
		System.out.println("Usuario : -" + user.getName());
		List<Incentive> incentives = new ArrayList<>();
		
		for(Sale sale : sales) {
			if(sale == null) {
				throw new NullPointerException("As vendas não podem estar vazias");
			}
			
			if(sale.getCustomer() == null) {
				throw new NullPointerException("O cliente não pode estar vazio");
			}
			
			if(sale.getEmployee() == null) {
				throw new NullPointerException("O vendedore não pode estar vazio");
			}
			
			if(sale.getProduct() == null) {
				throw new NullPointerException("O produto não pode estar vazio");
			}
			
			if(sale.getQuantity() == null) {
				throw new NullPointerException("A quantidade não pode estar vazia");
			}
			
			Customer customer = sale.getCustomer();
			
			Employee emp = sale.getEmployee();
			
			Product product = sale.getProduct();
			
			Integer quantity = sale.getQuantity();
			
			List<Function> empFunctions = emp.getFunctions();
			
			empFunctions.forEach(function -> {
				ApurationType cc = apurationTypeRepository.findByName("Conta Corrente");
				ApurationType nf = apurationTypeRepository.findByName("NF Serviço");
				BigDecimal ccIncentiveValue = BigDecimal.ZERO;
				BigDecimal nfsIncentiveValue = BigDecimal.ZERO;
				IncentiveValue value = incentiveValueRepository.findByCustomerAndProductAndFunction(customer, product, function);
				ccIncentiveValue = value.getCcValue().multiply(new BigDecimal(quantity));
				nfsIncentiveValue = value.getNfsValue().multiply(new BigDecimal(quantity));
				Incentive ccIncentive = new Incentive(null, null, user.getState(), emp.getPaymentMethod(), cc, emp, emp.getCpf(), ccIncentiveValue, function, customer, sale, user);
				Incentive nfsIncentive = new Incentive(null, null, user.getState(), emp.getPaymentMethod(), nf, emp, emp.getCpf(), nfsIncentiveValue, function, customer, sale, user);
				
				if (ccIncentive.getIncentiveValue().compareTo(BigDecimal.ZERO) != 0) {
				    incentives.add(ccIncentive);
				}

				if(nfsIncentive.getIncentiveValue().compareTo(BigDecimal.ZERO) != 0) {
					incentives.add(nfsIncentive);
				}
				

			});
		}
		  
		incentives.forEach(i -> {
			System.out.println("Vendedor : " + i.getEmployee().getName());
			System.out.println("Valor : " + i.getIncentiveValue());
			System.out.println("Metodo de pagamento: " + i.getPaymentMethod().getName());
			System.out.println("Apuração : " + i.getApurationType().getName());
			System.out.println("\n");
		});
		
		return incentives;
	}
	
	public Incentive calculeIncentive(Sale sale) {
		
		
		  
		
		return null;
	}
	
}





































