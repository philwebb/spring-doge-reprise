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

package doge.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import doge.photo.Photo;

@Entity
public class DogePhoto implements Photo {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	private DogeUser dogeUser;

	@Column(unique = true)
	private String uuid;

	@Lob
	private byte[] data;

	public DogePhoto(DogeUser dogeUser, String uuid, byte[] data) {
		this.dogeUser = dogeUser;
		this.uuid = uuid;
		this.data = data;
	}

	DogePhoto() {
	}

	public String getUuid() {
		return this.uuid;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(this.data);
	}

}
