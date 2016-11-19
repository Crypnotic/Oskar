package me.crypnotic.oskar.objects;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;
import org.json.JSONTokener;

import me.crypnotic.oskar.objects.constants.Outcome;

public class JSONConfig {

	private InputStream stream;
	private JSONObject root;

	public JSONConfig(InputStream stream) {
		this.stream = stream;
	}

	public Outcome init() {
		try {
			JSONTokener tokener = new JSONTokener(stream);
			this.root = new JSONObject(tokener);
			if (root == null) {
				return Outcome.FAILURE;
			}
			return Outcome.SUCCESSFUL;
		} catch (Exception exception) {
			exception.printStackTrace();
			return Outcome.FAILURE;
		}
	}

	public Outcome close() {
		try {
			stream.close();
			return Outcome.SUCCESSFUL;
		} catch (IOException exception) {
			exception.printStackTrace();
			return Outcome.FAILURE;
		}
	}

	public Object get(String path) {
		try {
			return root.get(path);
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}
}
