package com.project.BRP_backend;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class BrpBackendApplication {

	private static final Logger log = LoggerFactory.getLogger(BrpBackendApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BrpBackendApplication.class, args);
		log.info("Finished running the application");
	}
}
