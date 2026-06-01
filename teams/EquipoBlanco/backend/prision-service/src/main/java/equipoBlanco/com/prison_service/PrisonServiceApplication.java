package equipoBlanco.com.prison_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PrisonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrisonServiceApplication.class, args);
	}

}
