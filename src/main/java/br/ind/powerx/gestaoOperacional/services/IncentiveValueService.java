package br.ind.powerx.gestaoOperacional.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.IncentiveValue;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveValueRepository;

@Service
public class IncentiveValueService {

	@Autowired
	private IncentiveValueRepository valueRepository;
	
	public void save(IncentiveValue value) {
		valueRepository.save(value);
	}
	
	public List<IncentiveValue> findAll(){
		return valueRepository.findAll();
	}

	public List<IncentiveValue> findAllByProductId(Long id) {
		return valueRepository.findAllByProductId(id);
	}

	public void delete(IncentiveValue v) {
		valueRepository.delete(v);
	}

	public Optional<IncentiveValue> findById(Long incentiveValueId) {
		return valueRepository.findById(incentiveValueId);
	}
}
