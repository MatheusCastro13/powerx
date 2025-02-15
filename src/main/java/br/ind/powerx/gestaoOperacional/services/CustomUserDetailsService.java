package br.ind.powerx.gestaoOperacional.services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
        
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singleton(authority) 
        );
    }
}
