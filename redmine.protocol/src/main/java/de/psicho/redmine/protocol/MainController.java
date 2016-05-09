package de.psicho.redmine.protocol;

import org.springframework.boot.SpringApplication;

import de.psicho.redmine.protocol.config.AppConfig;

public class MainController {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(AppConfig.class, args);
	}
}
