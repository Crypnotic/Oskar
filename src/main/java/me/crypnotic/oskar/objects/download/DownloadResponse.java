package me.crypnotic.oskar.objects.download;

import java.io.File;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.crypnotic.oskar.objects.constants.Outcome;

@AllArgsConstructor
public class DownloadResponse {

	@Getter(lombok.AccessLevel.PUBLIC)
	private String videoId;
	@Getter(lombok.AccessLevel.PUBLIC)
	private Optional<File> value;
	@Getter(lombok.AccessLevel.PUBLIC)
	private Outcome outcome;
}
