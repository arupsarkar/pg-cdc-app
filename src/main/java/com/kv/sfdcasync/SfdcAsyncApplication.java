package com.kv.sfdcasync;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class SfdcAsyncApplication {
	private static final Logger LOG = LoggerFactory.getLogger(SfdcAsyncApplication.class);

	@GetMapping("/home")
	public String home(Map<String, Object> model) {
		String connSchema = "";
		try {
			// Class.forName("org.postgresql.Driver");
			// Connection conn = DriverManager.getConnection(dbUrl);
			// connSchema = conn.getSchema();
			// LOG.debug("db schema ", connSchema);
			model.put("schema", "DB Schema");
			return "home";
		} catch (Exception ex) {
			model.put("schema", "Error - " + connSchema);
			LOG.error("Application Error", ex.getCause());
			return "home";
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SfdcAsyncApplication.class, args);
	}

}
