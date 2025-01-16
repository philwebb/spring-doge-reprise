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

package doge;

import doge.domain.DogeUser;
import doge.domain.DogeUserRepository;
import doge.photo.DogePhotoManipulator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(DogeProperties.class)
public class DogeApplication {

	@Bean
	DogePhotoManipulator dogePhotoManipulator() {
		DogePhotoManipulator dogePhotoManipulator = new DogePhotoManipulator((text) -> {
			text.add("lamda", "abstractfactorybean", "java");
			text.add("spring", "annotations", "boot");
			text.add("code", "jvm", "kotlin");
			text.add("clean", "juergenized", "spring");
			text.add("js", "nonblocking", "wat");
		});
		return dogePhotoManipulator;
	}

	@Bean
	InitializingBean populateTestData(DogeUserRepository userRepository) {
		return () -> {
			userRepository.save(new DogeUser("philwebb", "Phil Webb"));
			userRepository.save(new DogeUser("joshlong", "Josh Long"));
			userRepository.findAll().forEach(System.err::println);
		};
	}

	@Bean
	CommandLineRunner commandLineRunner(DogeProperties properties) {
		return (args) -> System.out.println("\n\n" + properties.getWelcomeMessage() + "\n\n");
	}

	public static void main(String[] args) {
		SpringApplication.run(DogeApplication.class, args);
	}

}
