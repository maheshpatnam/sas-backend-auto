
/**
 * Instrucciones:
 * 
 *  - Crea un repo privado compartido sólo con dfleta en GitHub.
 *  - Realiza un commit al pasar cada caso test.
 *  - Sin este commit tras cada caso, no corrijo el examen.
 */

package sas.sdet.techtest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sas.sdet.techtest.repository.NotEnoughProException;
import sas.sdet.techtest.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * This is an application that manages a database of a bowling league or season, with the users of the service and tournaments.
 * bowling league or season, with the users of the service and the available tournaments (items).
 * available (items). The users make subscriptions (orders) to the * service to register for the championships.
 * Service to register for tournaments.
 * */

 @ExtendWith(SpringExtension.class)
 @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
 @Sql(statements = { "delete from t_ordenes", "delete from t_items", "delete from t_users",
  		"insert into t_users (user_name, user_prop) values ('Munson', 15)",
 		"insert into t_users (user_name, user_prop) values ('McCracken', 100)",
 		"insert into t_items (item_name, item_prop, item_type) values ('Murfreesboro Strike and Spare', 20, 'Torneo')",
 		"insert into t_items (item_name, item_prop, item_type) values ('Bowlerama Lanes Iowa', 7, 'Torneo')",
 		"insert into t_ordenes (ord_id, ord_user, ord_item) values (1,'Munson','Bowlerama Lanes Iowa')", })
 public class KingPinTest {
	@Autowired
	private TestRestTemplate restTemplate;

	private static final String ORDER_PATH = "/order?user=%s&item=%s";
	private static final String GET_USER_PATH = "/user/%s";
	private static final String KO = "KO";

	@Test
	public void shouldReturnOKWhenCreateOrderSuccessfully() {
		var url = String.format(ORDER_PATH, "Munson", "Bowlerama Lanes Iowa");
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		var responseEntity = restTemplate.exchange(url, POST, requestEntity, String.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualTo("OK");
	}

	@Test
	public void shouldReturnKOWhenUserDoesNotExists() {
		// when not able to load `unknown User`

		var url = String.format(ORDER_PATH, "unknown User", "Bowlerama Lanes Iowa");
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		var responseEntity = restTemplate.exchange(url, POST, requestEntity, String.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualTo(KO);
	}

	@Test
	public void shouldReturnKOWhenTournamentDoesNotExists() {
		// when not able to load `unknown tournament`
		var url = String.format(ORDER_PATH, "McCracken", "unknown tournament");
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		var responseEntity = restTemplate.exchange(url, POST, requestEntity, String.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualTo(KO);
	}

	@Test
	public void shouldReturnKOWhenNotEnoughProException() throws NotEnoughProException {
		// as dexterity of `Munson` (15) is less than professionalism of tournament `Murfreesboro Strike and Spare` (20)
		var url = String.format(ORDER_PATH, "Munson", "Murfreesboro Strike and Spare");
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		var responseEntity = restTemplate.exchange(url, POST, requestEntity, String.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualTo(KO);
	}


	@ParameterizedTest
	@CsvSource({
			"Munson, Munson, 15",
			"McCracken, McCracken, 100"
	})
	public void shouldGetUserDexterity(String name, String expectedName, int dexterity) {
		var url = String.format(GET_USER_PATH, name);
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		var responseEntity = restTemplate.exchange(url, GET, requestEntity, User.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		var user = responseEntity.getBody();
		assertThat(user).isNotNull();
		assertThat(user.getName()).isEqualTo(expectedName);
		assertThat(user.getDexterity()).isEqualTo(dexterity);
	}

	@Test
	public void shouldNotReturnUserWhenLoadingUserFails() {
		var url = String.format(GET_USER_PATH, "unknown user");
		HttpEntity<Void> requestEntity = new HttpEntity<>(null);
		var responseEntity = restTemplate.exchange(url, GET, requestEntity, User.class);

		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isNull();
	}
}


