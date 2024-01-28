package com.app.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.pojo.Authority;
import com.app.pojo.Customer;
import com.app.repositories.CustomerRepository;

@Service
public class UsernamePwdAuthenticationProvider implements AuthenticationProvider{

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String userName = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		
		List<Customer> customers = customerRepository.findByEmail(userName);
		if(customers.size()>0) {
			if(passwordEncoder.matches(pwd, customers.get(0).getPwd())) {
//				List<GrantedAuthority> authorities = new ArrayList<>();
//				authorities.add(new SimpleGrantedAuthority(customers.get(0).getRole()));
				return new UsernamePasswordAuthenticationToken(userName, pwd, getGrantedAuthorities(customers.get(0).getAuthorities()));
				//invokes AuthenticationProvider class. It will remove credentials (pwd), 
				//stored inside auth obj & set authenticated=true
			} else
				throw new BadCredentialsException("Invalid Password!");
		} else
			throw new BadCredentialsException("Username not found");
	}
	
	List<GrantedAuthority> getGrantedAuthorities(Set<Authority> authorities) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		for(Authority authority : authorities) {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
		}
		return grantedAuthorities;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
