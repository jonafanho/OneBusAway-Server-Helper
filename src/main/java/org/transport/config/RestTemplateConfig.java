package org.transport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public final class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
