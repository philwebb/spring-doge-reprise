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

package doge.service;

import java.io.IOException;
import java.util.UUID;

import doge.domain.DogePhoto;
import doge.domain.DogePhotoRepository;
import doge.domain.DogeUser;
import doge.photo.Photo;
import doge.photo.PhotoManipulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

@Service
public class DogePhotoService {

	private final DogePhotoRepository dogePhotoRepository;

	private final PhotoManipulator manipulator;

	@Autowired
	DogePhotoService(DogePhotoRepository dogePhotoRepository, PhotoManipulator manipulator) {
		this.dogePhotoRepository = dogePhotoRepository;
		this.manipulator = manipulator;
	}

	public DogePhoto get(DogeUser user, String uuid) {
		DogePhoto dogePhoto = this.dogePhotoRepository.findByDogeUserAndUuid(user.id(), uuid);
		Assert.state(dogePhoto != null, "Unable to find doge photo");
		return dogePhoto;
	}

	public DogePhoto add(DogeUser user, Photo photo) throws IOException {
		photo = this.manipulator.manipulate(photo);
		String uuid = UUID.randomUUID().toString();
		DogePhoto dogePhoto = new DogePhoto(null, user.id(), uuid, StreamUtils.copyToByteArray(photo.getInputStream()));
		return this.dogePhotoRepository.save(dogePhoto);
	}

}
