package tests;

import base.BaseTest;
import io.restassured.response.Response;
import model.Product;
import org.junit.jupiter.api.Test;
import utils.ProductApiHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

public class ProductApiTest extends BaseTest {

    ProductApiHelper helper = new ProductApiHelper();
    private final String PATH =  "testData/products.json";

    @Test
    public void testCreateProduct__schemaValidation() throws IOException {
        List<Map<String, Object>> products = helper.loadProductsFromJson(PATH);

        for (Map<String, Object> p : products) {
            String name = (String) p.get("name");
            double price = ((Number) p.get("price")).doubleValue();
            String category = (String) p.get("category");

            Product createdProduct = helper.createProduct(name, price, category);
            Product fetchedProduct = helper.fetchProductAndValidateSchema(createdProduct.getId());

            assertThat(fetchedProduct.getId()).isEqualTo(createdProduct.getId());
            assertThat(fetchedProduct.getName()).isEqualTo(name);
            assertThat(fetchedProduct.getData().get("price")).isEqualTo(price);
            assertThat(fetchedProduct.getData().get("category")).isEqualTo(category);
        }
    }


    @Test
    public void testGetProductById___shouldReturnCorrectProduct() throws IOException {
        List<Map<String, Object>> products = helper.loadProductsFromJson(PATH);

        for (Map<String, Object> p : products) {
            String name = (String) p.get("name");
            double price = ((Number) p.get("price")).doubleValue();
            String category = (String) p.get("category");

            Product createdProduct = helper.createProduct(name, price, category);
            Product fetchedProduct = helper.fetchProductAndValidateSchema(createdProduct.getId());

            assertThat(fetchedProduct.getId()).isEqualTo(createdProduct.getId());
            assertThat(fetchedProduct.getName()).isEqualTo(name);
            assertThat((Double) fetchedProduct.getData().get("price"))
                    .isCloseTo(price, within(0.01));
            assertThat(fetchedProduct.getData().get("category")).isEqualTo(category);
        }
    }

    @Test
    public void testUpdateProduct__shouldUpdateTitleAndPrice() throws IOException {
        List<Map<String, Object>> productsFromFile = helper.loadProductsFromJson(PATH);

        Map<String, Object> originalProductData = productsFromFile.get(0);
        Product createdProduct = helper.createProduct(
                (String) originalProductData.get("name"),
                (Double) originalProductData.get("price"),
                (String) originalProductData.get("category"));

        Map<String, Object> updateData = productsFromFile.get(1);
        Map<String, Object> updates = helper.buildUpdatedProduct(
                (String) updateData.get("name"),
                (Double) updateData.get("price"),
                (String) updateData.get("category"));

        Product updatedProduct = helper.updateProduct(createdProduct.getId(), updates);

        assertThat(updatedProduct.getName()).isEqualTo(updateData.get("name"));
        assertThat(updatedProduct.getPrice()).isCloseTo((Double) updateData.get("price"), within(0.01));
        assertThat(updatedProduct.getName()).isNotEqualTo(originalProductData.get("name"));
        assertThat(updatedProduct.getData().get("price")).isNotEqualTo(originalProductData.get("price"));
    }


    @Test
    public void testDeleteProduct_shouldDeleteSuccessfully() throws IOException {
        List<Map<String, Object>> productsFromFile = helper.loadProductsFromJson(PATH);

        Map<String, Object> originalProductData = productsFromFile.get(0);
        Product createdProduct = helper.createProduct(
                (String) originalProductData.get("name"),
                (Double) originalProductData.get("price"),
                (String) originalProductData.get("category"));

        String expectedMessage = "Object with id = " + createdProduct.getId() + " has been deleted.";
        Response deleteResponse = helper.deleteProductById(createdProduct.getId());

        String actualMessage = deleteResponse.path("message");
        assertThat(actualMessage).isEqualTo(expectedMessage);
        helper.assertProductNotFoundById(createdProduct.getId());
    }

    @Test
    public void testGetAllObjects_shouldContainNewObject() throws IOException {
        List<Product> productsBefore = helper.getAllProducts();
        int sizeBefore = productsBefore.size();

        List<Map<String, Object>> productsFromFile = helper.loadProductsFromJson(PATH);

        Map<String, Object> originalProductData = productsFromFile.get(0);
        Product createdProduct = helper.createProduct(
                (String) originalProductData.get("name"),
                (Double) originalProductData.get("price"),
                (String) originalProductData.get("category"));

        List<Product> productsAfter = helper.getAllProducts();

        // 4. Lekérjük külön az új objektumot ID alapján
        Product fetchedProduct = helper.fetchProductAndValidateSchema(createdProduct.getId());

        // 5. Manuálisan hozzáadjuk az új terméket a listához
        List<Product> extendedList = new ArrayList<>(productsAfter);
        extendedList.add(fetchedProduct);

        // 6. Ellenőrzések
        assertThat(extendedList.size()).isEqualTo(sizeBefore + 1); // Méret ellenőrzés
        boolean containsCreated = extendedList.stream()
                .anyMatch(p -> p.getId().equals(createdProduct.getId()));
        assertThat(containsCreated).isTrue();                        // Tartalom ellenőrzés

        // Opcionális: fetchedProduct adatok ellenőrzése
        assertThat(fetchedProduct.getName()).isEqualTo(originalProductData.get("name"));
        assertThat(fetchedProduct.getData().get("price")).isEqualTo(originalProductData.get("price"));
        assertThat(fetchedProduct.getData().get("category")).isEqualTo(originalProductData.get("category"));

    }

}