package example.weather;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import example.helper.FileLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WeatherAcceptanceTest {

    @LocalServerPort
    private int port;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Test
    public void shouldReturnCurrentWeather() throws Exception {
        wireMockRule.stubFor(get(urlPathEqualTo("/data/2.5/weather"))
                .withQueryParam("q", equalTo("Bangkok"))
                .withQueryParam("units", equalTo("metric"))
                .withQueryParam("appid", equalTo("some-test-api-key"))
                .willReturn(aResponse()
                        .withBody(FileLoader.read("classpath:weatherApiResponse.json"))
                        .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)));

        when()
                .get(String.format("http://localhost:%s/weather", port))
                .then()
                .statusCode(is(200))
                .body(containsString("Clouds"));
    }
}
