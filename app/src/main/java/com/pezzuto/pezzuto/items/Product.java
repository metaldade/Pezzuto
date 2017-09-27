package com.pezzuto.pezzuto.items;

/**
 * Created by dade on 31/03/17.
 */

public class Product extends PezzutoObject {
    private String category;
    private String code;
    private String marca;
    private String title;
    private double price;
    private String image;
    private String measure;
    private String description;
    private boolean featured;
    private String label;
    private int idCat;
    private int IVA;
    String thumbnail;
    private int id;
    private int idUser;
    private boolean isModifying = false;
    private boolean isRemoving = false;
    private double promotionPrice = 0;
    private int quantity = 0;
    public Product(int id, String code, String category, String title, String marca, double price, String measure, String thumbnail, String image, String description,
                   boolean featured, int IVA) {
        this.id = id;
        this.code = code;
        this.category = category;
        this.marca = marca;
        this.title = title;
        this.price = price;
        this.thumbnail = thumbnail;
        this.image = image;
        this.measure = measure;
        this.description = description;
        this.featured = featured;
        this.IVA = IVA;
    }
    public Product(int id, String title, String code, String category, double price, double promotionPrice, int IVA, int quantity) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.category = category;
        this.price = price;
        this.promotionPrice = promotionPrice;
        this.IVA = IVA;
        this.quantity = quantity;
    }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public String getImage() { return image; }
    public String getMeasure() { return measure; }
    public String getDescription() { return description; }
    public boolean isFeatured() { return featured; };
    public int getIdCat() { return idCat; }
    public int getIVA() { return IVA; }
    public int getIdUser() { return idUser; }
    public String getMarca() { return marca; }
    public String getCode() { return code; }
    public void setPromotionPrice(double p) {
        promotionPrice = p;
    }
    public double getPromotionPrice() {
        return promotionPrice;
    }
    public void add() { quantity++; }
    public void remove() { if (quantity > 0) quantity--; }
    public int getQuantity() { return quantity; }
    public void setCategory(String category) { this.category = category; }
    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }
    public String getThumbnail() { return thumbnail; }
    public int getId() { return id; }
    public void goModify(boolean isModifying) {
        this.isModifying = isModifying;
    }
    public void goRemove(boolean isRemoving) {
        this.isRemoving = isRemoving;
    }
    public boolean isRemoving() {
        return isRemoving;
    }
    public boolean isModifying() {
        return isModifying;
    }
}
