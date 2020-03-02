package com.jp.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jp.repository.UserRepository;

import static java.util.Collections.emptyList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

	private UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.jp.entity.User applicationUser = userRepository.findByUsername(username);
		if (applicationUser == null) {
			throw new UsernameNotFoundException(username);
		}
		return new User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
	}

	public String generateQRUrl(User user) {
		try {
			return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
					"TestAppAuth", user.getUsername(), user.getPassword(), "TestAppAuth"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
// TODO Auto-generated catch block
			return null;
		}
	}

	public String generateQRUrl(com.jp.entity.User user) {
// TODO Auto-generated method stub
		try {
			return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
					"TestAppAuth", user.getEmail(), user.getSecret(), "TestAppAuth"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
// TODO Auto-generated catch block
			return null;
		}
	}

	public com.jp.entity.User updateUser2FA(boolean use2FA) {
		Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("curAuth:-" + curAuth.getPrincipal());
		com.jp.entity.User currentUser = (com.jp.entity.User) curAuth.getPrincipal();
		currentUser.setGfa(use2FA);
		currentUser = userRepository.save(currentUser);

		Authentication auth = new UsernamePasswordAuthenticationToken(currentUser, currentUser.getPassword(),
				curAuth.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		return currentUser;
	}

	public com.jp.entity.User updateU2FA(boolean use2FA, com.jp.entity.User user) {
		System.out.println("curAuth:-" + " " + user.getUsername());
		com.jp.entity.User currentUser = userRepository.findByUsername(user.getUsername());
		currentUser.setGfa(use2FA);
		currentUser = userRepository.save(currentUser);

		return currentUser;
	}
}