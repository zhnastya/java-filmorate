package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.serializer.FilmSerializer;
import ru.yandex.practicum.filmorate.model.serializer.UserSerializer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Month;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
class FilmorateApplicationTests {
	ConfigurableApplicationContext context;
	HttpClient client = HttpClient.newHttpClient();
	Gson gson = new GsonBuilder()
			.registerTypeAdapter(User.class, new UserSerializer())
			.registerTypeAdapter(Film.class, new FilmSerializer())
			.create();

	@BeforeEach
	protected void startApplication() {
		context = SpringApplication.run(FilmorateApplication.class);
	}


	@AfterEach
	protected void exitApplication() {
		SpringApplication.exit(context);
	}


	private static Stream<Arguments> emailValidator() {
		return Stream.of(
				arguments("email@email.ru", 200),
				arguments("email @email.ru", 400),
				arguments("emailemail.ru", 400),
				arguments("", 400),
				arguments(null, 400)
		);
	}


	private static Stream<Arguments> loginValidator() {
		return Stream.of(
				arguments("login", 200),
				arguments("log in", 400),
				arguments("", 400),
				arguments(null, 400)
		);
	}


	private static Stream<Arguments> birthValidator() {
		return Stream.of(
				arguments("1999-05-06", 200),
				arguments("2025-12-12", 400),
				arguments("2025", 400),
				arguments("", 400),
				arguments(null, 400)
		);
	}


	private static Stream<Arguments> idValidator() {
		return Stream.of(
				arguments(1, 200),
				arguments(-2, 400),
				arguments(null, 200)
		);
	}


	private static Stream<Arguments> nameFilmValidator() {
		return Stream.of(
				arguments("Name of Film", 200),
				arguments("", 400),
				arguments(null, 400)
		);
	}


	private static Stream<Arguments> descFilmValidator() {
		return Stream.of(
				arguments("a".repeat(200), 200),
				arguments("a".repeat(100), 200),
				arguments("a".repeat(300), 400),
				arguments("", 200)
		);
	}


	private static Stream<Arguments> releaseFilmValidator() {
		return Stream.of(
				arguments("1895-12-28", 200),
				arguments("1895-12-27", 400),
				arguments("1895-12-29", 200),
				arguments(null, 400)
		);
	}


	private static Stream<Arguments> durationFilmValidator() {
		return Stream.of(
				arguments(1, 200),
				arguments(-2, 400),
				arguments(0, 400)
		);
	}


	HttpResponse<String> userPostRequest(User user) throws IOException, InterruptedException {
		String json = gson.toJson(user);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/users"))
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.header("Content-Type", "application/json")
				.build();

		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}


	HttpResponse<String> filmPostRequest(Film film) throws IOException, InterruptedException {
		String json = gson.toJson(film);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/films"))
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.header("Content-Type", "application/json")
				.build();

		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}


	@ParameterizedTest
	@MethodSource("emailValidator")
	void shouldCheckEmail(String email, int status) throws IOException, InterruptedException {
		User user = new User(null, email, "login", "name",
				LocalDate.of(1996, Month.JUNE, 23));

		assertEquals(userPostRequest(user).statusCode(), status);
	}


	@ParameterizedTest
	@MethodSource("loginValidator")
	void shouldCheckLogin(String login, int status) throws IOException, InterruptedException {
		User user = new User(null, "email1@email.ru", login, "name",
				LocalDate.of(1996, Month.JUNE, 23));

		assertEquals(userPostRequest(user).statusCode(), status);
	}


	@ParameterizedTest
	@MethodSource("birthValidator")
	void shouldCheckBirth(String date, int status) throws IOException, InterruptedException {
		LocalDate date2 = date != null && Pattern.matches("\\d{4}(-)\\d{2}(-)\\d{2}", date)
				? LocalDate.parse(date) : null;
		User user = new User(null, "email1@email.ru", "login", "name", date2);

		assertEquals(userPostRequest(user).statusCode(), status);
	}


	@ParameterizedTest
	@MethodSource("idValidator")
	void shouldCheckLogin(Integer id, int status) throws IOException, InterruptedException {
		User user = new User(id, "email1@email.ru", "login", "name",
				LocalDate.of(1996, Month.JUNE, 23));

		assertEquals(userPostRequest(user).statusCode(), status);
	}


	@Test
	void shouldGetUsers() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/users"))
				.GET()
				.build();

		HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(response1.statusCode(), 200);
	}


	@Test
	void shouldPutUsers() throws IOException, InterruptedException {
		userPostRequest(new User(0, "email1@email.ru", "login", "name",
				LocalDate.of(1996, Month.JUNE, 23)));
		User newUser = new User(1, "email1@email.ru", "newLogin", "newName",
				LocalDate.of(1996, Month.JUNE, 23));
		String json = gson.toJson(newUser);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/users"))
				.PUT(HttpRequest.BodyPublishers.ofString(json))
				.header("Content-Type", "application/json")
				.build();

		HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(response1.statusCode(), 200);
		assertEquals(response1.body(), json);
	}


	@ParameterizedTest
	@MethodSource("nameFilmValidator")
	void shouldCheckNameFilm(String name, int status) throws IOException, InterruptedException {
		Film film = new Film(0, name, "desc", LocalDate.of(1900, 12, 23), 5);

		assertEquals(filmPostRequest(film).statusCode(), status);
	}


	@ParameterizedTest
	@MethodSource("descFilmValidator")
	void shouldCheckDescFilm(String desc, int status) throws IOException, InterruptedException {
		Film film = new Film(0, "name", desc, LocalDate.of(1900, 12, 23), 5);

		assertEquals(filmPostRequest(film).statusCode(), status);
	}


	@ParameterizedTest
	@MethodSource("releaseFilmValidator")
	void shouldCheckReleaseFilm(String release, int status) throws IOException, InterruptedException {
		LocalDate date = release != null ? LocalDate.parse(release) : null;
		Film film = new Film(0, "name", "desc", date, 5);

		assertEquals(filmPostRequest(film).statusCode(), status);
	}


	@ParameterizedTest
	@MethodSource("durationFilmValidator")
	void shouldCheckDurationFilm(int duration, int status) throws IOException, InterruptedException {
		Film film = new Film(0, "name", "desc",
				LocalDate.of(1990, 12, 12), duration);

		assertEquals(filmPostRequest(film).statusCode(), status);
	}


	@Test
	void shouldGetFilms() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/films"))
				.GET()
				.build();

		HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(response1.statusCode(), 200);
	}


	@Test
	void shouldPutFilms() throws IOException, InterruptedException {
		filmPostRequest(new Film(0, "name", "desc",
				LocalDate.of(1990, 12, 12), 30));
		Film film = new Film(1, "newName", "newDesc",
				LocalDate.of(1990, 12, 12), 30);
		String json = gson.toJson(film);
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/films"))
				.PUT(HttpRequest.BodyPublishers.ofString(json))
				.header("Content-Type", "application/json")
				.build();

		HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());

		assertEquals(response1.statusCode(), 200);
		assertEquals(response1.body(), json);
	}
}
