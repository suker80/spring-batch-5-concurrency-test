package com.example.demo;

public class SampleData {
    @Override
    public String toString() {
        return "SampleData{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }

    public SampleData() {
    }

    public SampleData(int id, String value) {
        this.id = id;
        this.value = value;
    }

    private int id;
    private String value;

    public void setId(int id) {
        this.id = id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    // Getters, setters, and constructors
}
