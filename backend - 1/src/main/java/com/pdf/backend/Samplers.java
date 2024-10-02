package com.pdf.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
@SpringBootApplication
public class Samplers {

	public static void main(String[] args) {
    SpringApplication.run(Samplers.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Configuration
	@EnableScheduling
	static
	class SchedulerConfig {}

	@Service
	static
	class TestScheduler {

		@Scheduled(fixedRate = 60000)
		public void runScheduler() {
			System.out.println("Scheduler running at: " + System.currentTimeMillis());
		}
	}
}
