package me.crypnotic.oskar.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class Interwebs {

	public static Optional<String> read(String address) {
		try {
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if (connection.getResponseCode() != 200) {
				return Optional.empty();
			}
			BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = input.readLine()) != null) {
				builder.append(line);
			}
			input.close();
			return Optional.of(builder.toString());
		} catch (Exception exception) {
			exception.printStackTrace();
			return Optional.empty();
		}
	}

	public static Optional<File> download(String address, File output) {
		try {
			if (output.exists()) {
				return Optional.of(output);
			}
			URL url = new URL(address);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			if (connection.getResponseCode() != 200) {
				return Optional.empty();
			}
			BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
			FileOutputStream target = new FileOutputStream(output);
			long size = connection.getContentLength();
			long downloaded = 0L;
			int length = -1;
			byte[] buffer = new byte[1024];
			while ((length = input.read(buffer)) > -1) {
				target.write(buffer, 0, length);
				downloaded += length;
			}
			if (input != null) {
				input.close();
			}
			if (output != null) {
				target.close();
			}

			if (output.exists()) {
				if (downloaded == size) {
					return Optional.of(output);
				} else {
					Files.delete(output);
					return Optional.empty();
				}
			} else {
				return Optional.empty();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return Optional.empty();
		}
	}
}
