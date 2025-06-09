package com.example.realestate.domain.model;

public class Property implements java.io.Serializable {
    private int propertyId;
    private String description;
    private String title;
    private double price;
    private String location;
    private String image;
    private String type;
    private int bedrooms;
    private int bathrooms;

    private boolean isFeatured;

    private double discount;
    private String area;

    public Property() {
        // Default constructor
    }

    public Property(int propertyId, String description, String title, double price, String location,
            String image, String type, int bedrooms, int bathrooms, String area, boolean isFeatured, double discount) {
        this.propertyId = propertyId;
        this.description = description;
        this.title = title;
        this.price = price;
        this.location = location;
        this.image = image;
        this.type = type;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.area = area;
        this.isFeatured = isFeatured;
        this.discount = discount;
    }

    // Getters and setters
    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    // Additional convenience methods
    public String getId() {
        return String.valueOf(propertyId);
    }

    public String getImageUrl() {
        return image;
    }
}
