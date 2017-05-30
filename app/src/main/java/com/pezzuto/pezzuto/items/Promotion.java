package com.pezzuto.pezzuto.items;

/**
 * Created by dade on 24/03/17.
 */

public class Promotion {
    private String oldPrice;
    private String title;
    private String price;
    public Promotion (int id, String title, String oldPrice, String price) {
        this.price = price;
        this.oldPrice = oldPrice;
        this.title = title;
    }
    public String getOldPrice() { return oldPrice; }
    public String getPrice() { return price; }
    public String getTitle() { return title; }
}
