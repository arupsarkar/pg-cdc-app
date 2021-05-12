package com.kv.sfdcasync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class SfdcAsyncApplication {

	@GetMapping("/home")
	public String home() {
		return "home";
	}

	public static void main(String[] args) {
		SpringApplication.run(SfdcAsyncApplication.class, args);
	}

}
