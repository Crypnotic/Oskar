package me.crypnotic.oskar.utilities;

import java.util.ArrayList;
import java.util.List;

public class Multisets {

	public static <T> List<T> transform(T[] array) {
		return transform(array, 0);
	}

	public static <T> List<T> transform(T[] array, int start) {
		return transform(array, start, array.length);
	}

	public static <T> List<T> transform(T[] array, int start, int end) {
		List<T> list = new ArrayList<T>();
		for (int i = start; i < array.length && i < end; i++) {
			list.add(array[i]);
		}
		return list;
	}

	public static String join(List<String> list) {
		return join(list, " ");
	}

	public static String join(List<String> list, String seperator) {
		return join(list, seperator, 0);
	}

	public static String join(List<String> list, String seperator, int start) {
		return join(list, seperator, start, list.size());
	}

	public static String join(List<String> list, String seperator, int start, int end) {
		StringBuilder builder = new StringBuilder();
		for (int i = start; i < list.size() && i < end; i++) {
			builder.append(list.get(i));
			if (i < list.size() - 1) {
				builder.append(seperator);
			}
		}
		return builder.toString();
	}
}
