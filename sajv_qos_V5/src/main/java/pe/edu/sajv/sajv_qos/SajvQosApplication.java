package pe.edu.sajv.sajv_qos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class SajvQosApplication {

	public static void main(String[] args) {
		SpringApplication.run(SajvQosApplication.class, args);
	}

}
