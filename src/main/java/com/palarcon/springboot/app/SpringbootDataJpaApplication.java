package com.palarcon.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.palarcon.springboot.app.models.service.IUploadFileService;

@SpringBootApplication
public class SpringbootDataJpaApplication implements CommandLineRunner{
	@Autowired
	IUploadFileService uploadService;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDataJpaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		uploadService.deleteAll();
		uploadService.init();
		
	}
}
