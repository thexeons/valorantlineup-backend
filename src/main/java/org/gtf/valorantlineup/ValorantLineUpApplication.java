package org.gtf.valorantlineup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ValorantLineUpApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValorantLineUpApplication.class, args);
	}

}
