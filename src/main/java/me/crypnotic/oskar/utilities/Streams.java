package me.crypnotic.oskar.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

public class Streams {

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
}
