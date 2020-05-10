package app.controllers;

import app.models.maps.Line;

public class LineManagement {
    private Line line;

    public void startUp(Line line){
        this.line=line;
    }
}
