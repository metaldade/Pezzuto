package com.pezzuto.pezzuto.items;

import java.util.Date;
import java.util.List;

/**
 * Created by dade on 24/03/17.
 */

public class Promprod {
    private String title;
    private String description;
    private String category;
    private Date validaDal;
    private Date validaAl;
    private boolean esaurimento;
    private String image;
    private String label;
    private int idCat;
    private Date createdAt;
    private Date updatedAt;
    private boolean active;
    private int idUser;
    private int id;
    private List<Product> products;
    public Promprod(int id, String category, String title, String description, String image, Date validaDal, Date validaAl,
                    boolean esaurimento) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.image = image;
        this.validaDal = validaDal;
        this.validaAl = validaAl;
        this.esaurimento = esaurimento;
    }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getValidaDal() { return validaDal; }
    public Date getValidaAl() { return validaAl; }
    public boolean isEsaurimento() { return esaurimento; }
    public String getCategory() { return category;}
    public String getImage() { return image; }
    public void setProducts(List<Product> prods) {
        products = prods;
    }
    public List<Product> getProducts() {
        return products;
    }
    public void setLabel(String label) { this.label = label; }
    public String getLabel() { return label; }
    public int getId() { return id; }
}
