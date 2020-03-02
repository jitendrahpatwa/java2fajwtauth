package com.jp.controller;

import java.time.Instant;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jp.config.JwtTokenUtil;
import com.jp.entity.Roles;
import com.jp.entity.User;
import com.jp.lib.ResponseMaker;
import com.jp.lib.RoleEnum;
import com.jp.lib.UserData;
import com.jp.repository.RolesRepository;
import com.jp.repository.UserRepository;
import com.jp.service.UserDetailsServiceImpl;

import javassist.tools.web.BadHttpRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticateController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RolesRepository rolesRepository;

	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public AuthenticateController(BCryptPasswordEncoder bCryptPasswordEncoder) {
		// TODO Auto-generated constructor stub
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	// to login users
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, Object> authenticationRequest)
			throws Exception {
		authenticate(authenticationRequest.get("username").toString(),
				authenticationRequest.get("password").toString());
		final UserDetails userDetails = userDetailsService
				.loadUserByUsername(authenticationRequest.get("username").toString());
		final String token = jwtTokenUtil.generateToken(userDetails);
		ResponseMaker mapResp = new ResponseMaker();
		mapResp.setStatus("ok");
		mapResp.setMessage("Logged in successfully");
		Map<String, Object> mapRespBody = new HashMap<String, Object>();
		// mapRespBody.put("user",
		// userRepository.findByUsername(userDetails.getUsername()));
		User user = userRepository.findByUsername(userDetails.getUsername());
		if (!user.getGfa()) {
			mapResp.setMessage("2FA is required");
			mapRespBody.put("token", token);
		}
		mapRespBody.put("user", userData(user));
		mapResp.setBody(mapRespBody);
		return ResponseEntity.ok(mapResp);
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	// To register users
	@RequestMapping(value = "/add/user", method = RequestMethod.POST)
	public <T> ResponseEntity addUser(HttpServletResponse response, @RequestBody Map<String, Object> reqBody) {
		ResponseMaker mapResp = new ResponseMaker();
		try {
			System.out.println("La' " + reqBody.get("name"));
			User user = new User();
			if (!reqBody.containsKey("username")) {
				throw new BadHttpRequest();

			}
			Optional<User> optionalUser = userRepository.findOneByUsername(reqBody.get("username").toString());

			// System.out.println("optionalUser "+optionalUser.get());
			if (!optionalUser.isPresent()) {
				user.setUsername(reqBody.get("username").toString());
				user.setName(reqBody.get("name").toString());
				user.setEmail(reqBody.get("email").toString());
				user.setPassword(bCryptPasswordEncoder.encode(reqBody.get("password").toString()));
				user.setActive(true);
				user.setGfa(false);
				user.setCreated(Instant.now());
				user.setUpdated(Instant.now());

				Roles newRoles = new Roles();
				Optional<Roles> roles = rolesRepository.findOneByRole(RoleEnum.ADMIN.toString());
				Optional<Roles> roles2 = rolesRepository.findOneByRole(RoleEnum.USER.toString());
				System.out.println("roles " + roles + roles2);
				if (!roles.isPresent()) {
					newRoles.setRole(RoleEnum.ADMIN.toString());
					rolesRepository.save(newRoles);
				} else {
					if (!roles2.isPresent()) {
						newRoles.setRole(RoleEnum.USER.toString());
						rolesRepository.save(newRoles);
					} else {
						newRoles = roles2.get();
					}
				}
				user.setRoles(newRoles);
				userRepository.save(user);

				mapResp.setStatus("ok");
				mapResp.setBody(userData(user));
				mapResp.setMessage(user.getName() + " Added Successfully!");
			} else {

				mapResp.setStatus("fail");
				mapResp.setMessage("Already Added!");
			}
		} catch (BadHttpRequest e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			mapResp.setStatus("fail");
			mapResp.setMessage("Already Added!");
		}
		return ResponseEntity.ok().body(mapResp);
	}

	// Below will validate the JWT authorization to set the user is logged in or not
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public ResponseEntity<?> myDetails(HttpServletRequest request) {
		ResponseMaker mapResp = new ResponseMaker();
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration headerNames = request.getHeaderNames();
		String username;
		Boolean records = false;
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			if (key.equals("x-user")) {
				username = value;
				Optional<User> optionalUser = userRepository.findOneByUsername(username);
				if (optionalUser.isPresent()) {
					records = true;
					map.put("user", userData(optionalUser.get()));
					if (!optionalUser.get().getGfa()) {
						map.put("qa", userDetailsService.generateQRUrl(optionalUser.get()));
					}
				}
				mapResp.setBody(map);
			}
		}
		if (records) {
			mapResp.setStatus("ok");
			mapResp.setMessage("Details Found Successfully!");
		} else {
			mapResp.setStatus("fail");
			mapResp.setMessage("Details not found!");
		}
		return ResponseEntity.ok().body(mapResp);
	}

	@RequestMapping(value = "/gfa", method = RequestMethod.POST)
	public ResponseEntity<?> enableGFA(HttpServletRequest request, @RequestBody Map<String, Object> reqBody) {
		ResponseMaker mapResp = new ResponseMaker();
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration headerNames = request.getHeaderNames();
		String username;
		Boolean records = false;
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			if (key.equals("x-user")) {
				username = value;
				Optional<User> optionalUser = userRepository.findOneByUsername(username);
				if (optionalUser.isPresent()) {
					records = true;
					map.put("user", userData(optionalUser.get()));
					User user = userDetailsService.updateU2FA(Boolean.valueOf(reqBody.get("gfa").toString()),
							optionalUser.get());
					System.out.print("user: " + user);
					if (Boolean.valueOf(reqBody.get("gfa").toString())) {
						map.put("gfa", true);
					}
				}
				mapResp.setBody(map);
			}
		}
		if (records) {
			mapResp.setStatus("ok");
			mapResp.setMessage("Details Updated Successfully!");
		} else {
			mapResp.setStatus("fail");
			mapResp.setMessage("Details not found!");
		}
		return ResponseEntity.ok().body(mapResp);
	}

	@RequestMapping(value = "/verify/gfa", method = RequestMethod.POST)
	public ResponseEntity<?> verifyGFA(HttpServletRequest request, @RequestBody Map<String, Object> reqBody)
			throws Exception {
		ResponseMaker mapResp = new ResponseMaker();
		Map<String, Object> map = new HashMap<String, Object>();
		if (reqBody.containsKey("username")) {
			Optional<User> optionalUser = userRepository.findOneByUsername(reqBody.get("username").toString());
			Totp totp = new Totp(optionalUser.get().getSecret());
			System.out.println("TOTP:" + reqBody.get("code").toString());
			if (!totp.verify(reqBody.get("code").toString())) {
				// TOTP don't match
				mapResp.setStatus("fail");
				map.put("message", "Not verified");
			} else {
				mapResp.setStatus("ok");
				map.put("message", "Verified");
			}
			// System.out.println("TOTP:" + totp.verify(totp.now()));

			authenticate(reqBody.get("username").toString(), reqBody.get("password").toString());
			final UserDetails userDetails = userDetailsService.loadUserByUsername(reqBody.get("username").toString());
			final String token = jwtTokenUtil.generateToken(userDetails);
			// ResponseMaker mapResp = new ResponseMaker();
			mapResp.setStatus("ok");
			mapResp.setMessage("Logged in successfully");
			Map<String, Object> mapRespBody = new HashMap<String, Object>();
			// mapRespBody.put("user",
			// userRepository.findByUsername(userDetails.getUsername()));
			User user = userRepository.findByUsername(userDetails.getUsername());
			if (!user.getGfa()) {
				mapRespBody.put("token", token);
			}
			mapRespBody.put("user", userData(user));
			mapResp.setBody(mapRespBody);
			// return ResponseEntity.ok(mapResp);

			// mapResp.setBody(map);
		}
		return ResponseEntity.ok().body(mapResp);
	}

	private Map<String, Object> userData(User user) {
		Map<String, Object> userData = new HashMap<String, Object>();
		userData.put("active", user.getActive());
		userData.put("email", user.getEmail());
		userData.put("name", user.getName());
		userData.put("username", user.getUsername());
		userData.put("gfa", user.getGfa());
		userData.put("roles", user.getRoles());

		return userData;
	}
}