package me.crypnotic.oskar;

public class OskarBootstrap {

	private static Oskar oskar;

	public static void main(String[] args) throws InterruptedException {
		OskarBootstrap.oskar = new Oskar();

		oskar.start();
	}

	public static Oskar getOskar() {
		return oskar;
	}
}
