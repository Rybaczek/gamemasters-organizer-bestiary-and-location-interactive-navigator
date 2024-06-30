package goblin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan
public class GamemastersOrganizerBestiaryAndLocationInteractiveNavigatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamemastersOrganizerBestiaryAndLocationInteractiveNavigatorApplication.class, args);
	}
}
