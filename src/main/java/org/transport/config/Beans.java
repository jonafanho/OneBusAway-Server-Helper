package org.transport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Component
public final class Beans {

	private final int BUFFER_SIZE = 10 * 1024 * 1024; // 10 MB

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(BUFFER_SIZE))
				.clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
				.build();
	}
}
