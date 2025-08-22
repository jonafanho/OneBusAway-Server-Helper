package org.transport.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
@Component
public final class ConsoleCommandRunner implements ApplicationRunner {

	private final ConfigurableApplicationContext configurableApplicationContext;

	public ConsoleCommandRunner(ConfigurableApplicationContext configurableApplicationContext) {
		this.configurableApplicationContext = configurableApplicationContext;
	}

	@Override
	public void run(ApplicationArguments applicationArguments) {
		final Thread thread = new Thread(() -> {
			try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					if ("stop".equalsIgnoreCase(line.trim())) {
						log.info("Stopping application");
						SpringApplication.exit(configurableApplicationContext, () -> 0);
						break;
					}
				}
			} catch (Exception e) {
				log.error("Failed to read console input", e);
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
}
