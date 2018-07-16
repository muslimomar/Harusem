package com.example.william.harusem.models;

/**
 * Created by eminesa on 16.07.2018.
 */

public class UserData {

    private String englishLevel;
    private String turkishLevel;
    private String arabicLevel;

    private String country;

    public UserData(String englishLevel, String turkishLevel, String arabicLevel, String country) {
        this.englishLevel = englishLevel;
        this.turkishLevel = turkishLevel;
        this.arabicLevel = arabicLevel;
        this.country = country;
    }

    public String getEnglishLevel() {
        return englishLevel;
    }

    public void setEnglishLevel(String englishLevel) {
        this.englishLevel = englishLevel;
    }

    public String getTurkishLevel() {
        return turkishLevel;
    }

    public void setTurkishLevel(String turkishLevel) {
        this.turkishLevel = turkishLevel;
    }

    public String getArabicLevel() {
        return arabicLevel;
    }

    public void setArabicLevel(String arabicLevel) {
        this.arabicLevel = arabicLevel;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
