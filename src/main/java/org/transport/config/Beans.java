package org.transport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public final class Beans {

	private final int BUFFER_SIZE = 10 * 1024 * 1024; // 10 MB

	@Bean
	public WebClient webClient() {
		return WebClient.builder().codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(BUFFER_SIZE)).build();
	}
}
