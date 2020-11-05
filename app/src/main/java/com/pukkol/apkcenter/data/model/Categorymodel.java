package com.pukkol.apkcenter.data.model;

public class Categorymodel {

    private String key;
    private String value;

    public Categorymodel(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Categorymodel{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}