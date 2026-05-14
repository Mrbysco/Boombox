package com.mrbysco.boombox.util;

import com.google.gson.JsonObject;
import com.mrbysco.boombox.BoomboxMod;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import static com.mrbysco.boombox.BoomboxMod.USER_AGENT;

/**
 * This class was based on the class from ResourcefulLib which is licensed under MIT.
 */
public class WebUtils {

	private static final HttpClient CLIENT = HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_2)
			.followRedirects(HttpClient.Redirect.NORMAL)
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	@Nullable
	public static String get(String url, boolean onlySuccess) {
		return get(url, HttpResponse.BodyHandlers.ofString())
				.filter(response -> !onlySuccess || (response.statusCode() >= 200 && response.statusCode() < 300))
				.map(HttpResponse::body)
				.orElse(null);
	}

	@Nullable
	public static String get(String url) {
		return get(url, false);
	}

	@Nullable
	public static JsonObject getJson(String url, boolean onlySuccess) {
		try {
			return BoomboxMod.GSON.fromJson(get(url, onlySuccess), JsonObject.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Nullable
	public static JsonObject getJson(String url) {
		return getJson(url, false);
	}

	public static <T> Optional<HttpResponse<T>> get(String url, HttpResponse.BodyHandler<T> handler) {
		try {
			HttpRequest request = HttpRequest.newBuilder(new URI(url))
					.GET()
					.version(HttpClient.Version.HTTP_2)
					.header("User-Agent", USER_AGENT)
					.build();

			return Optional.of(CLIENT.send(request, handler));
		} catch (Exception ignored) {
		}
		return Optional.empty();
	}
}
