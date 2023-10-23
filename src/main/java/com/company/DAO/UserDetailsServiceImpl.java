package com.company.DAO;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.company.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
	private userRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	User user=	userRepository.getUserByUserName(username);
	if(user==null) {
		throw new UsernameNotFoundException("Could not found user name!");
	}
	CustomUserDetail customUserDetail = new CustomUserDetail(user);
	
		
		return customUserDetail;
	}

}
