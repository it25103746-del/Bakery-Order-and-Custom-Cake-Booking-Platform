package com.bakery.model;

import java.util.List;

public class Product extends BakeryRecord {
    private String productName;
    private String category;
    private String price;
    private String details;
    private String availability;

    public Product() {
    }

    public Product(String id, String productName, String category, String price, String details, String availability) {
        super(id);
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.details = details;
        this.availability = availability;
    }

    @Override
    public String getDisplayName() {
        return productName + " - Rs. " + price;
    }

    @Override
    public List<String> toFileFields() {
        return List.of(productName, category, price, details, availability);
    }

    @Override
    public void applyFileFields(List<String> fields) {
        productName = fields.get(0);
        category = fields.get(1);
        price = fields.get(2);
        details = fields.get(3);
        availability = fields.get(4);
    }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }
}
