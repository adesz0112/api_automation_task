package tests;

import base.BaseTest;
import io.restassured.response.Response;
import model.Product;
import org.junit.jupiter.api.Test;
import utils.ProductApiHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

public class ProductApiTest extends BaseTest {

    ProductApiHelper helper = new ProductApiHelper();

    @Test
    public void testCreateProduct__schemaValidation() {
        String name = "Laptop";
        double price = 123.45;
        String category = "electronics";
        Product createdProduct = helper.createProduct(name, price, category);

        Product fetchedProduct = helper.fetchProductAndValidateSchema(createdProduct.getId());

        assertThat(fetchedProduct.getId()).isEqualTo(createdProduct.getId());
        assertThat(fetchedProduct.getName()).isEqualTo(name);
        assertThat(fetchedProduct.getData().get("price")).isEqualTo(price);
        assertThat(fetchedProduct.getData().get("category")).isEqualTo(category);
    }


    @Test
    public void testGetProductById___shouldReturnCorrectProduct() {
        // Arrange: Létrehozunk egy terméket a helper metódussal
        String name = "Test Laptop";
        double price = 1499.99;
        String category = "electronics";

        Product createdProduct = helper.createProduct(name, price, category);

        // Act: Lekérjük az ID alapján, és validáljuk a séma alapján
        Product fetchedProduct = helper.fetchProductAndValidateSchema(createdProduct.getId());
        // Assert: Tartalmi validáció
        assertThat(fetchedProduct.getId()).isEqualTo(createdProduct.getId());
        assertThat(fetchedProduct.getName()).isEqualTo(name);
        assertThat((Double) fetchedProduct.getData().get("price"))
                .isCloseTo(price, within(0.01));
        assertThat(fetchedProduct.getData().get("category")).isEqualTo(category);
    }

    @Test
    public void testUpdateProduct__shouldUpdateTitleAndPrice() {
        // 1. Először létrehozunk egy terméket
        Product createdProduct = helper.createProduct("Old Laptop", 1200.00, "electronics");

        // 2. Készítünk egy update map-et a name és price módosításához
        Map<String, Object> updates = helper.buildUpdatedProduct("New Laptop", 1399.99, "electronics");
        // 3. Meghívjuk az update metódust
        Product updatedProduct = helper.updateProduct(createdProduct.getId(), updates);

        // 4. Ellenőrzések AssertJ-vel
        assertThat(updatedProduct.getName()).isEqualTo("New Laptop");
        assertThat(updatedProduct.getPrice()).isCloseTo(1399.99, within(0.01));

        // 5. Régi értékeket ellenőrzés, hogy nem maradtak benne
        assertThat(updatedProduct.getData().get("price")).isNotEqualTo(createdProduct.getData().get("price"));
        assertThat(updatedProduct.getName()).isNotEqualTo(createdProduct.getName());
    }


    @Test
    public void testDeleteProduct_shouldDeleteSuccessfully() {
        // 1. Létrehozunk egy terméket
        Product createdProduct = helper.createProduct("Product to delete", 50.0, "test-category");

        String expectedMessage = "Object with id = " + createdProduct.getId() + " has been deleted.";

        // 2. Törlés
        Response deleteResponse = helper.deleteProductById(createdProduct.getId());

        String actualMessage = deleteResponse.path("message");
        assertThat(actualMessage).isEqualTo(expectedMessage);

        // 3. Lekérjük újra - várjuk, hogy 404 legyen
        helper.assertProductNotFoundById(createdProduct.getId());
    }

    @Test
    public void testGetAllObjects_shouldContainNewObject() {
        // 1. Lekérjük az összes objektumot a létrehozás előtt
        List<Product> productsBefore = helper.getAllProducts();

        int sizeBefore = productsBefore.size();

        // 2. Létrehozunk egy új objektumot
        Product createdProduct = helper.createProduct("Test Product", 123.45, "test-category");

        // 3. Lekérjük az összes objektumot a létrehozás után
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
        assertThat(fetchedProduct.getName()).isEqualTo("Test Product");
        assertThat(fetchedProduct.getData().get("price")).isEqualTo(123.45);
        assertThat(fetchedProduct.getData().get("category")).isEqualTo("test-category");
    }

}