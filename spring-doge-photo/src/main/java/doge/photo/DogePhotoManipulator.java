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

package doge.photo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

/**
 * A {@link PhotoManipulator} to add Doge images.
 */
public class DogePhotoManipulator implements PhotoManipulator {

	private static final int IMAGE_WIDTH = 300;

	private BufferedImage dogeLogo;

	private final TextOverlays textOverlays = new TextOverlays();

	public DogePhotoManipulator(Consumer<Text> text) {
		this(readClassImage("/doge-logo.png"), text);
	}

	private DogePhotoManipulator(BufferedImage dogeLogo, Consumer<Text> text) {
		Assert.notNull(dogeLogo, "'dogeLogo' must not be null");
		Assert.notNull(dogeLogo, "'text' must not be null");
		this.dogeLogo = dogeLogo;
		text.accept(this.textOverlays::add);
	}

	@Override
	public Photo manipulate(Photo photo) throws IOException {
		BufferedImage sourceImage = readImage(photo);
		BufferedImage destinationImage = manipulate(sourceImage);
		return () -> getInputStream(destinationImage);
	}

	private BufferedImage manipulate(BufferedImage sourceImage) {
		double aspectRatio = sourceImage.getHeight() / (double) sourceImage.getWidth();
		int height = (int) Math.floor(IMAGE_WIDTH * aspectRatio);
		BufferedImage destinationImage = new BufferedImage(IMAGE_WIDTH, height, BufferedImage.TYPE_INT_RGB);
		render(sourceImage, destinationImage);
		return destinationImage;
	}

	private void render(BufferedImage sourceImage, BufferedImage destinationImage) {
		Graphics2D destinationGraphics = destinationImage.createGraphics();
		try {
			setGraphicsHints(destinationGraphics);
			renderBackground(sourceImage, destinationImage, destinationGraphics);
			renderOverlay(destinationImage, destinationGraphics);
		} finally {
			destinationGraphics.dispose();
		}
	}

	private void setGraphicsHints(Graphics2D graphics) {
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
	}

	private void renderBackground(BufferedImage sourceImage, BufferedImage destinationImage,
			Graphics2D destinationGraphics) {
		destinationGraphics.drawImage(sourceImage, 0, 0, IMAGE_WIDTH, destinationImage.getHeight(), null);
	}

	private void renderOverlay(BufferedImage image, Graphics2D graphics) {
		this.textOverlays.random().render(image, graphics);
		int y = image.getHeight() - this.dogeLogo.getHeight();
		graphics.drawImage(this.dogeLogo, 0, y, null);
	}

	private InputStream getInputStream(BufferedImage image) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);
		ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("png").next();
		imageWriter.setOutput(imageOutputStream);
		imageWriter.write(null, new IIOImage(image, null, null), null);
		ImageIO.write(image, "png", outputStream);
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	private static BufferedImage readClassImage(String name) {
		ClassPathResource resource = new ClassPathResource(name);
		return readImage(resource::getInputStream);
	}

	private static BufferedImage readImage(Photo photo) {
		try (InputStream inputStream = photo.getInputStream()) {
			return ImageIO.read(inputStream);
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	@FunctionalInterface
	public interface Text {

		void add(String very, String so, String such);

	}

	private static class TextOverlays {

		private final List<TextOverlay> overlays = new ArrayList<>();

		private final Random random = new Random();

		void add(String very, String so, String such) {
			this.overlays.add(new TextOverlay(very, so, such));
		}

		TextOverlay random() {
			return this.overlays.get(this.random.nextInt(this.overlays.size()));
		}

	}

	private static class TextOverlay {

		private final String very;

		private final String so;

		private final String such;

		TextOverlay(String very, String so, String such) {
			this.very = very;
			this.so = so;
			this.such = such;
		}

		void render(BufferedImage image, Graphics2D g) {
			double r = image.getHeight() / 448.0;
			renderText(g, "wow", 32, Color.MAGENTA, 25, r * 43);
			renderText(g, "very " + this.very, 29, Color.GREEN, 105, r * 115);
			renderText(g, "so " + this.so, 20, Color.MAGENTA, 25, r * 330);
			renderText(g, "such " + this.such, 30, Color.ORANGE, 125, r * 385);
		}

		private void renderText(Graphics2D g, String text, int fontSize, Paint paint, double x, double y) {
			Font font = new Font("Comic Sans MS", Font.BOLD, fontSize);
			GlyphVector vector = font.createGlyphVector(g.getFontRenderContext(), text.toCharArray());
			Shape shape = vector.getOutline((int) x, (int) y);
			g.setStroke(new BasicStroke(0.5f));
			g.setPaint(paint);
			g.fill(shape);
			g.setColor(Color.BLACK);
			g.draw(shape);
		}

	}

}
