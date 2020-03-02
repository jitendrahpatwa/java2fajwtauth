package com.jp.testapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = "com.jp")
@EnableAutoConfiguration
@EntityScan("com.jp.entity")
@EnableJpaRepositories(basePackages = "com.jp.repository")
public class Springwebuidemo1Application {

	public static void main(String[] args) {
		SpringApplication.run(Springwebuidemo1Application.class, args);
	}

//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/auth/*").allowedOrigins("http://localhost:4200");
//			}
//		};
//	}
}
