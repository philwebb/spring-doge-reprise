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

package doge.controller;

import java.io.IOException;
import java.net.URI;

import doge.domain.DogePhoto;
import doge.domain.DogeUser;
import doge.domain.DogeUserRepository;
import doge.photo.Photo;
import doge.photo.PhotoResource;
import doge.service.DogePhotoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/users")
public class UsersRestController {

	private final DogeUserRepository userRepository;

	private final DogePhotoService dogeService;

	@Autowired
	public UsersRestController(DogeUserRepository userRepository, DogePhotoService dogeService) {
		this.userRepository = userRepository;
		this.dogeService = dogeService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public Iterable<DogeUser> getUsers() {
		return this.userRepository.findAll();
	}

	@RequestMapping(method = RequestMethod.POST, value = "{username}/doge")
	public ResponseEntity<?> postDogePhoto(@PathVariable String username, @RequestParam MultipartFile file,
			UriComponentsBuilder uriBuilder) throws IOException {
		DogeUser user = getUser(username);
		Photo sourcePhoto = file::getInputStream;
		DogePhoto dogePhoto = this.dogeService.add(user, sourcePhoto);
		URI uri = uriBuilder.path("/users/{username}/doge/{uuid}")
			.buildAndExpand(username, dogePhoto.getUuid())
			.toUri();
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.GET, value = "{username}/doge/{uuid}", produces = MediaType.IMAGE_PNG_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Resource> getDogePhoto(@PathVariable String username, @PathVariable String uuid) {
		DogeUser user = getUser(username);
		Photo photo = this.dogeService.get(user, uuid);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<>(new PhotoResource(photo), headers, HttpStatus.OK);
	}

	private DogeUser getUser(String username) {
		DogeUser user = this.userRepository.findByUsername(username);
		Assert.state(user != null, "Unable to find user " + username);
		return user;
	}

}
