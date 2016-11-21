package me.crypnotic.oskar.objects.constants;

import java.util.Optional;

public enum Outcome {
	SUCCESSFUL, FAILURE, UNKNOWN;

	private String message;

	public Boolean isSuccessful() {
		return (this == Outcome.SUCCESSFUL);
	}

	public Optional<String> getMessage() {
		return Optional.of(message);
	}

	public Outcome setMessage(String message, Object... values) {
		this.message = String.format(message, values);
		return this;
	}

	public String toString() {
		return super.toString().substring(0, 1).toUpperCase() + super.toString().substring(1).toLowerCase();
	}
}
