package com.n2s.infotech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InfotechApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfotechApplication.class, args);
	}

}
