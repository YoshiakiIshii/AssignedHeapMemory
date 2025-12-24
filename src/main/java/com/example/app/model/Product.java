package com.example.app.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @CsvBindByPosition(position = 0)
    @CsvBindByName(column = "id")
    private String id;
    
    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "name")
    private String name;
    
    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "manufacturer")
    private String manufacturer;
    
    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "price")
    private String price;
    
    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "createdAt")
    private String createdAt;
    
    @CsvBindByPosition(position = 5)
    @CsvBindByName(column = "updatedAt")
    private String updatedAt;
    
    @CsvBindByPosition(position = 6)
    @CsvBindByName(column = "deleted")
    private String deleted;

    public boolean isDeleted() {
        return "true".equalsIgnoreCase(deleted);
    }

    public void setDeleted(boolean deleted) {
        this.deleted = Boolean.toString(deleted);
    }
}
