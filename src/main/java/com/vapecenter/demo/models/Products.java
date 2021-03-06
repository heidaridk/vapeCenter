package com.vapecenter.demo.models;

public class Products {
    int productId, stock, fk_categoryId;
    String name, description, pictureLink, categoryName;
    int active;
    double price;

    public Products() {
    }

    public Products(int productId, int active, int stock, String name, String description, String pictureLink, double price) {
    }
    public Products(int productId, String name, double price) {
        this.productId = productId;
        this.name = name;
    }

    public Products(int productId, int stock, int fk_categoryId, String name, String description, String pictureLink, int active, double price) {
        this.productId = productId;
        this.stock = stock;
        this.fk_categoryId = fk_categoryId;
        this.name = name;
        this.description = description;
        this.pictureLink = pictureLink;
        this.active = active;
        this.price = price;
    }

    public Products(int productId, int stock, String name, String description, String pictureLink, String categoryName, int active, double price) {
        this.productId = productId;
        this.stock = stock;
        this.name = name;
        this.description = description;
        this.pictureLink = pictureLink;
        this.categoryName = categoryName;
        this.active = active;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getFk_categoryId() {
        return fk_categoryId;
    }

    public void setFk_categoryId(int fk_categoryId) {
        this.fk_categoryId = fk_categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "Products{" +
                "productId=" + productId +
                ", stock=" + stock +
                ", fk_categoryId=" + fk_categoryId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pictureLink='" + pictureLink + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", active=" + active +
                ", price=" + price +
                '}';
    }
}
