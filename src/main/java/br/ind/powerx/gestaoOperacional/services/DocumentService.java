package br.ind.powerx.gestaoOperacional.services;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.IncentiveDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.SaleDTO;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;

@Service
public class DocumentService {

	private final SaleRepository saleRepository;
	
	private final IncentiveRepository incentiveRepository;
	
	
	@Autowired
	public DocumentService(SaleRepository saleRepository, IncentiveRepository incentiveRepository) {
		this.saleRepository = saleRepository;
		this.incentiveRepository = incentiveRepository;
	}
	
	public List<Integer> findAllDocumentNumbers(){
		List<Integer> salesDocumentNumbers = saleRepository.findDistinctDocumentNumbers();
		List<Integer> incentivesDocumentNumbers = incentiveRepository.findDistinctDocumentNumbers();
		
		Set<Integer> allDocumentNumbers = new HashSet<>();
		allDocumentNumbers.addAll(incentivesDocumentNumbers);
		allDocumentNumbers.addAll(salesDocumentNumbers);

		return allDocumentNumbers.stream()
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList());
	}
	
	public Map<String, Object> getDocumentDetails(Integer documentNumber){
		List<Sale> sales = saleRepository.findByDocumentNumber(documentNumber);
		List<Incentive> incentives = incentiveRepository.findBySaleDocumentNumber(documentNumber);
		
		Customer customer = sales.get(0).getCustomer();
		
		List<SaleDTO> salesDtoList = sales.stream().map(sale -> new SaleDTO(
				sale.getReferenceDate(),
				sale.getCustomer().getFantasyName(),
				sale.getEmployee().getName(),
				sale.getProduct().getProductName(),
				sale.getProduct().getProductCode(),
				sale.getQuantity(),
				sale.getDocumentNumber()
		)).collect(Collectors.toList());
		
		List<IncentiveDTO> dtoList = incentives.stream().map(incentive -> new IncentiveDTO(
		        incentive.getReferenceDate(),
		        incentive.getState().toString(),
		        incentive.getApurationType().getName(),
		        incentive.getPaymentMethod().getName(),
		        incentive.getCpf(),
		        incentive.getEmployee().getName(),
		        incentive.getIncentiveValue(),
		        incentive.getEmployeeFunction().getName(),
		        incentive.getCustomer().getFantasyName(),
		        incentive.getCustomer().getCnpj()
		)).collect(Collectors.toList());
		
		
		Map<String, Object> details = new HashMap<>();
		details.put("sales", salesDtoList);
		details.put("incentives", dtoList);
		details.put("fantasyName", customer.getFantasyName());
		details.put("cnpj", customer.getCnpj());
		details.put("method", dtoList.get(0).getPaymentMethod());
		details.put("date", dtoList.get(0).getReferenceDate());
		details.put("state", dtoList.get(0).getState());
		
		
		return details;
	}

	public List<Integer> findAllDocumentNumbersByUser(User user) {
		List<Integer> incentivesDocumentNumbers = incentiveRepository.findDistinctDocumentNumbersByUserId(user.getId());
		
		return incentivesDocumentNumbers.stream()
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList());
	}

	public Map<Integer, String> getCustomersByDocument(List<Integer> documentNumbers) {
		Map<Integer, String> documentCustomer = new HashMap<>();
		for(Integer documentNumber : documentNumbers) {
			String customerName = incentiveRepository.findBySaleDocumentNumber(documentNumber).get(0).getCustomer().getFantasyName();
			documentCustomer.put(documentNumber, customerName);
		}
		return documentCustomer;
	}
	
	public Map<Integer, String> getCustomersByDocument(Integer documentNumber) {
		Map<Integer, String> documentCustomer = new HashMap<>();
		String customerName = incentiveRepository.findBySaleDocumentNumber(documentNumber).get(0).getCustomer().getFantasyName();
		documentCustomer.put(documentNumber, customerName);
		return documentCustomer;
	}
}











