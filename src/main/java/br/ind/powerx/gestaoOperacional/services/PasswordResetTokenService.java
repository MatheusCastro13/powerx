package br.ind.powerx.gestaoOperacional.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.PasswordResetToken;
import br.ind.powerx.gestaoOperacional.repositories.PasswordResetTokenRepository;

@Service
public class PasswordResetTokenService {

	@Autowired
	private PasswordResetTokenRepository tokenRepository;
	
	public void savePasswordResetToken(PasswordResetToken token) {
        tokenRepository.save(token);
    }

    public Optional<PasswordResetToken> findPasswordResetToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        if (resetToken.isPresent() && resetToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            return resetToken;
        }
        return Optional.empty();
    }

    public void deletePasswordResetToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }
    
    public void deletePasswordResetTokenByUserId(Long id) {
        tokenRepository.deleteByUserId(id);
    }

	public Optional<PasswordResetToken> findPasswordResetTokenByUser(Long id) {
		Optional<PasswordResetToken> resetToken = tokenRepository.findByUserId(id);
		
		if (!resetToken.isPresent()) {
            return Optional.empty();
        }
		
		if (!resetToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
			deletePasswordResetTokenByUserId(id);
			return Optional.empty();
        }
		
        if (resetToken.isPresent() && resetToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            return resetToken;
        }
        
        return Optional.empty();
	}
}





