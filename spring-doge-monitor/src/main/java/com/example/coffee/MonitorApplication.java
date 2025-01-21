/*
 * Copyright 2014-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.coffee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@SpringBootApplication(proxyBeanMethods = false)
@ConfigurationPropertiesScan
@EnableScheduling
public class MonitorApplication {

	private static final Logger log = LoggerFactory.getLogger(MonitorApplication.class);

	private final MonitorProperties properties;

	private final RestClient restClient;

	private final Alerter alerter;

	public MonitorApplication(MonitorProperties properties, RestClient.Builder restClientBuilder, Alerter alerter) {
		this.properties = properties;
		this.restClient = restClientBuilder.build();
		this.alerter = alerter;
	}

	@Scheduled(fixedRateString = "1s")
	void check() {
		log.info("Checking " + this.properties.url());
		try {
			var responseEntity = this.restClient.get().uri(this.properties.url()).retrieve().toBodilessEntity();
			log.info("Got response status " + responseEntity.getStatusCode());
		} catch (RestClientException ex) {
			this.alerter.triggerAlert();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(MonitorApplication.class, args);
	}

}
