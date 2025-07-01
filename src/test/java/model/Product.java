package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    private String id;
    private String name;
    private Map<String, Object> data;

    public Product() {
    }

    public Product(String name, Map<String, Object> data) {
        this.name = name;
        this.data = data;
    }

    public Product(String name, double price, String category) {
        this.name = name;
        this.data = new HashMap<>();
        this.data.put("price", price);
        this.data.put("category", category);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public double getPrice() {
        return ((Number) data.get("price")).doubleValue();
    }

    public String getCategory() {
        return (String) data.get("category");
    }
}
