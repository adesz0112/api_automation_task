package base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.lessThan;

public abstract class BaseTest {

    public static RequestSpecification requestSpec;
    public static ResponseSpecification responseSpec;
    public static ResponseSpecification responseSpec404;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://api.restful-api.dev";


        requestSpec = new RequestSpecBuilder()
                .setContentType(JSON)
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(JSON)
                .expectResponseTime(lessThan(5000L))
                .build();

        responseSpec404 = new ResponseSpecBuilder()
                .expectStatusCode(404)
                .expectResponseTime(lessThan(5000L))
                .build();
    }
}