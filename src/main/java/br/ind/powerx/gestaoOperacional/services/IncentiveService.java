package br.ind.powerx.gestaoOperacional.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;

@Service
public class IncentiveService {

	@Autowired
	private IncentiveRepository incentiveRepository;
	
	public List<Incentive> findAll(){
		return incentiveRepository.findAll();
	}
	
	public Optional<Incentive> findById(Long id){
		return incentiveRepository.findById(id);
	}
	
	public void save(Incentive incentive) {
		incentiveRepository.save(incentive);
	}
	
	public List<Incentive> findByUser(User user){
		return incentiveRepository.findByUser(user);
	}
	
	public Page<Incentive> findAll(Pageable pageable) {
	    return incentiveRepository.findAll(pageable);
	}

	public Page<Incentive> findByUser(User user, Pageable pageable) {
	    return incentiveRepository.findByUser(user, pageable);
	}
}
