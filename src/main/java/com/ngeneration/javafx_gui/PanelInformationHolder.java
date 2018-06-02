package com.ngeneration.javafx_gui;

import com.ngeneration.Simulation;

class PanelInformationHolder {
    private String title;
    private String template;
    private String name;
    private final Simulation simulation;

    PanelInformationHolder(Simulation simulation, String template, String panelName, String title) {
        this.template = template;
        this.name = panelName;
        this.title = title;
        this.simulation = simulation;
    }

    public String getTitle() {
        return title;
    }

    public String getTemplate() {
        return template;
    }

    public String getName() {
        return name;
    }
}
