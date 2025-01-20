package br.ind.powerx.gestaoOperacional.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.util.SwilePaymentOrder;
import br.ind.powerx.gestaoOperacional.util.YouCardPaymentOrder;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SpreadsheetService {

	
	@Autowired
	private IncentiveRepository incentiveRepository;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private ApurationTypeRepository apurationTypeRepository;

	public ByteArrayOutputStream generatePaymentOrder(LocalDate dataInicial, LocalDate dataFinal, Long clienteId, Long tipoApuracaoId) throws IOException {
		Customer customer = customerRepository.findById(clienteId)
				.orElseThrow(()-> new EntityNotFoundException("Cliente não encontrado"));
		
		ApurationType apurationType = apurationTypeRepository.findById(tipoApuracaoId)
				.orElseThrow(()-> new EntityNotFoundException("Apuração não encontrada"));
		
		List<Incentive> incentives = incentiveRepository.findByReferenceDateBetweenAndCustomerAndApurationType(dataInicial, dataFinal, customer, apurationType);
		String PaymentMethodName = incentives.get(0).getEmployee().getPaymentMethod().getName().toLowerCase();
		
		switch(PaymentMethodName) {
			case "swile" -> {
				return new SwilePaymentOrder(incentives).generateOrder();
			}
			case "you card" ->{
				return new YouCardPaymentOrder(incentives).generateOrder();
			}
		}
		
		return null;
	}
	
	public ByteArrayOutputStream generatePaymentOrder(LocalDate dataInicial, LocalDate dataFinal, Long apurationTypeId, String stateString) throws IOException {
		ApurationType apurationType = apurationTypeRepository.findById(apurationTypeId)
				.orElseThrow(()-> new EntityNotFoundException("Apuração não encontrado"));
		
		State state = State.valueOf(stateString);
		
		List<Incentive> incentives = incentiveRepository.findByReferenceDateBetweenAndApurationTypeAndState(dataInicial, dataFinal, apurationType, state);
		String PaymentMethodName = incentives.get(0).getEmployee().getPaymentMethod().getName().toLowerCase();
		
		switch(PaymentMethodName) {
			case "swile" -> {
				return new SwilePaymentOrder(incentives).generateOrder();
			}
			case "you card" ->{
				return new YouCardPaymentOrder(incentives).generateOrder();
			}
		}
		
		return null;
	}
}
