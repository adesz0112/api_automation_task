package utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import model.Product;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static base.BaseTest.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class ProductApiHelper {

    private final String path = "schemas/product-schema.json";

    public Product fetchProductAndValidateSchema(String productId) {
        return given()
                .spec(requestSpec)
                .when()
                .get("/objects/" + productId)
                .then()
                .spec(responseSpec)
                .assertThat()
                .body(matchesJsonSchemaInClasspath(path))
                .extract()
                .as(Product.class);
    }

    public Product createProduct(String name, double price, String category) {
        Map<String, Object> data = new HashMap<>();
        data.put("price", price);
        data.put("category", category);

        Map<String, Object> productMap = new HashMap<>();
        productMap.put("name", name);
        productMap.put("data", data);

        return given()
                .spec(requestSpec)
                .body(productMap)
                .when()
                .post("/objects")
                .then()
                .spec(responseSpec)
                .extract()
                .as(Product.class);
    }

    public Product updateProduct(String productId, Map<String, Object> updates) {
        return given()
                .spec(requestSpec)
                .body(updates)
                .when()
                .patch("/objects/" + productId)
                .then()
                .spec(responseSpec)
                .assertThat()
                .body(matchesJsonSchemaInClasspath(path))
                .extract()
                .as(Product.class);
    }

    public Response deleteProductById(String productId) {
        return given()
                .spec(requestSpec)
                .when()
                .delete("/objects/" + productId)
                .then()
                .spec(responseSpec)
                .extract()
                .response();
    }

    public void assertProductNotFoundById(String productId) {
        given()
                .spec(requestSpec)
                .when()
                .get("/objects/" + productId)
                .then()
                .spec(responseSpec404);
    }

    public List<Product> getAllProducts() {
        return given()
                .spec(requestSpec)
                .when()
                .get("/objects")
                .then()
                .spec(responseSpec)
                .extract()
                .jsonPath()
                .getList("", Product.class);
    }

    public Map<String, Object> buildUpdatedProduct(String name, double price, String category) {
        Map<String, Object> data = new HashMap<>();
        data.put("price", price);
        data.put("category", category);

        Map<String, Object> update = new HashMap<>();
        update.put("name", name);
        update.put("data", data);

        return update;
    }


    public List<Map<String, Object>> loadProductsFromJson(String resourcePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        return mapper.readValue(is, new TypeReference<List<Map<String, Object>>>(){});
    }


}
