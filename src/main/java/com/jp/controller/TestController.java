package com.jp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
public class TestController {

	public TestController() {
		// TODO Auto-generated constructor stub
	}

	// Test method
	@GetMapping(value="welcome")
	public <T> ResponseEntity<T> welcome() {
		Map mapResp = new HashMap();
		mapResp.put("status", "ok");
		mapResp.put("message", "Successful!");
		return ResponseEntity.ok().body(mapResp); 
	}
}
