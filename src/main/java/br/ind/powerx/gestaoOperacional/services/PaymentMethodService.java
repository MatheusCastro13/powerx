package br.ind.powerx.gestaoOperacional.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.dtos.PaymentMethodDTO;
import br.ind.powerx.gestaoOperacional.repositories.PaymentMethodRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class PaymentMethodService {

	@Autowired
	private PaymentMethodRepository methodRepository;
	
	public List<PaymentMethod> findAll(){
		return methodRepository.findAll();
	}

	public void save(PaymentMethodDTO methodDTO) {
		PaymentMethod method = new PaymentMethod();
		method.setName(methodDTO.name());
		methodRepository.save(method);
	}

	public void update(Long id, PaymentMethod method) {
		PaymentMethod paymentMethod = methodRepository.findById(id)
				.orElseThrow(()-> new EntityNotFoundException("Metodo não encontrado"));
		
		paymentMethod.setName(method.getName());
		methodRepository.save(method);
	}
}









