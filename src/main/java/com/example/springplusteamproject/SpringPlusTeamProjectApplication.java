package com.example.springplusteamproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class SpringPlusTeamProjectApplication {

	public static void main(String[] args) {

		// dotenv 환경변수 설정
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		// 모든 항목 시스템 속성으로 설정
		dotenv.entries().forEach(entry ->
			System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(SpringPlusTeamProjectApplication.class, args);
	}

}
