package com.ketevan.vanloadingoptimiser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VanloadingoptimiserApplication {

	public static void main(String[] args) {
		SpringApplication.run(VanloadingoptimiserApplication.class, args);
	}

}
