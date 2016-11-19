package me.crypnotic.oskar.objects;

@FunctionalInterface
public interface OskarCallback<T> {

	void call(T result);
}
