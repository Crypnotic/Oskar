package me.crypnotic.oskar.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

public class Files {

	public static Optional<FileInputStream> toStream(File file) {
		try {
			if (file == null) {
				return Optional.empty();
			}
			return Optional.of(new FileInputStream(file));
		} catch (IOException exception) {
			exception.printStackTrace();
			return Optional.empty();
		}
	}

	public static Optional<File> copy(InputStream input, String output) {
		return copy(input, new File(output));
	}

	public static void delete(File... files) {
		for (File file : files) {
			if (file.exists()) {
				file.delete();
			}
		}
	}

	public static Optional<File> copy(InputStream input, File output) {
		try {
			if (!output.exists() || output.isDirectory()) {
				if (input == null) {
					return Optional.empty();
				}
				output.getParentFile().mkdirs();
				output.createNewFile();
				OutputStream stream = new FileOutputStream(output);
				byte[] buffer = new byte[1024];
				int length = input.read(buffer);
				while (length != -1) {
					stream.write(buffer, 0, length);
					length = input.read(buffer);
				}
				stream.close();
				input.close();
			}
			return Optional.of(output);
		} catch (IOException exception) {
			return Optional.empty();
		}
	}
}
