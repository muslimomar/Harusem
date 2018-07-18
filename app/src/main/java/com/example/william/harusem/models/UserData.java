package com.example.william.harusem.models;

public class UserData {
    private String motherLanguage;
    private String learningLanguage;
    private String selectedLanguageLevel;
    private String userCountry;

    public UserData(String motherLanguage, String learningLanguage, String selectedLanguageLevel, String userCountry) {
        this.motherLanguage = motherLanguage;
        this.learningLanguage = learningLanguage;
        this.selectedLanguageLevel = selectedLanguageLevel;
        this.userCountry = userCountry;
    }

    public String getMotherLanguage() {
        return motherLanguage;
    }

    public void setMotherLanguage(String motherLanguage) {
        this.motherLanguage = motherLanguage;
    }

    public String getLearningLanguage() {
        return learningLanguage;
    }

    public void setLearningLanguage(String learningLanguage) {
        this.learningLanguage = learningLanguage;
    }

    public String getSelectedLanguageLevel() {
        return selectedLanguageLevel;
    }

    public void setSelectedLanguageLevel(String selectedLanguageLevel) {
        this.selectedLanguageLevel = selectedLanguageLevel;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }
}