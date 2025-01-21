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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@Profile("default")
class PiAlerterConfiguration {

	@Bean
	PiAlerter alerter(MonitorProperties properties) {
		return new PiAlerter(properties.pin());
	}

	static class PiAlerter implements Alerter {

		private static Logger log = LoggerFactory.getLogger(PiAlerter.class);

		private final String pin;

		private final AtomicBoolean madeCoffee = new AtomicBoolean();

		PiAlerter(int pin) {
			this.pin = String.valueOf(pin);
			pinctrl(false);
		}

		@Override
		public void triggerAlert() {
			log.warn("WAKE UP!!!");
			if (this.madeCoffee.compareAndSet(false, true)) {
				makeCoffee();
			}
		}

		private void makeCoffee() {
			try {
				toggle();
				Thread.sleep(TimeUnit.SECONDS.toMillis(5));
				toggle();
			} catch (InterruptedException ex) {
			}
		}

		private void toggle() throws InterruptedException {
			pinctrl(true);
			Thread.sleep(500);
			pinctrl(false);
		}

		private void pinctrl(boolean high) {
			try {
				new ProcessBuilder("pinctrl", "set", this.pin, "op", (!high) ? "dl" : "dh").start();
			} catch (IOException ex) {
				throw new UncheckedIOException(ex);
			}
		}

	}

}
