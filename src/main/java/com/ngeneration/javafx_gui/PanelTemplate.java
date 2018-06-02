package com.ngeneration.javafx_gui;

public enum PanelTemplate {
    CAR_CONTROLLER("controlPanel.fxml", "Car controller");

    private final String templateFile;
    private String defaultTitle;

    PanelTemplate(String templateFile, String defaultTitle) {
        this.templateFile = templateFile;
        this.defaultTitle = defaultTitle;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public String getTitle() {
        return defaultTitle;
    }
}
