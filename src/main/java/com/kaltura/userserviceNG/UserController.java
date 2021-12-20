package com.kaltura.userserviceNG;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.kaltura.userserviceNG.bean.Login;
import com.kaltura.userserviceNG.bean.Register;


@RestController
public class UserController {
	@Autowired
	private RestTemplate restTemplate;
		
	@PostMapping("/register")
	public ResponseEntity<Register> register(@RequestBody Register register) throws Exception {
		final String baseUrl = "https://api.frs1.ott.kaltura.com/api_v3/service/ottuser/action/register";		
		ResponseEntity<Register> response = restTemplate.postForEntity(baseUrl, register, Register.class);
		return response;
	}
	
	@PostMapping("/login")
	public ResponseEntity<Object> login( @RequestBody Login login) throws Exception {
		final String baseUrl = "https://api.frs1.ott.kaltura.com/api_v3/service/ottuser/action/login";		
		ResponseEntity<Object> response = restTemplate.postForEntity(baseUrl, login, Object.class);
		return response;
	}
	
}
