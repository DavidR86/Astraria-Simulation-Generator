package sample.controllers;/*
* =====================================================================
* ==      Created by davrockenzahn19        ==    Date: 3/15/18   ==
* =====================================================================
* ==      Project: Astraria-Generator-Tool    ==
* =====================================================================

*/

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import sample.Main;

public class TitleScreenController {
    public Button newSimulationButton;
    public VBox mainBox;
    private Main main;

    public void hello(ActionEvent actionEvent) {
        main.ChangeToNewSimulation();
    }

    public void setMain(Main main){
        this.main = main;
    }
}
