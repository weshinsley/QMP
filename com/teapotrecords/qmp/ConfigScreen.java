package com.teapotrecords.qmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ConfigScreen extends Stage {
  QMP parent;

  final TextField tf_x = new TextField();
  final TextField tf_y = new TextField();
  final TextField tf_w = new TextField();
  final TextField tf_h = new TextField();
  
  public ConfigScreen(QMP parent) {
    super();
    this.parent = parent;
    setTitle("Configure Scren Geometry");
    initModality(Modality.APPLICATION_MODAL);
    
    int gridy = 0;
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20, 20, 20, 20));
    
    // Detect screens in use and allow copying geometry
    
    grid.add(new Label("Detect Fullscreens"), 0, gridy);
    Button b_detect = new Button("Detect");
    grid.add(b_detect, 1, gridy++);
    
    b_detect.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent e) {
        List<String> choices = new ArrayList<>();
        int scr_no = 1;
        ObservableList<Screen> screens = Screen.getScreens();
        for (Screen scr : screens) {
          Rectangle2D bounds = scr.getBounds();
          choices.add(scr_no + ": " + (int) bounds.getWidth() + "x"
              + (int) bounds.getHeight() + " at (" + (int) bounds.getMinX()
              + "," + (int) bounds.getMinY() + ")");
          scr_no++;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(choices.size() - 1), choices);
        dialog.setTitle("Screen detection");
        dialog.setHeaderText("Screens Detected:");
        dialog.setContentText("Choose screen: ");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
          String r = result.get();
          int pick = Integer.parseInt(r.substring(0, r.indexOf(":"))) - 1;
          Screen scr = screens.get(pick);
          Rectangle2D bounds = scr.getBounds();
          tf_w.setText(String.valueOf((int) bounds.getWidth()));
          tf_h.setText(String.valueOf((int) bounds.getHeight()));
          tf_x.setText(String.valueOf((int) bounds.getMinX()));
          tf_y.setText(String.valueOf((int) bounds.getMinY()));
          parent.conf.saveCurrentConfig();
        }
      }
    });
    
    
 // Location line
    final HBox hb_location = new HBox();
    grid.add(new Label("Location (x,y)"), 0, gridy);
    tf_x.setMaxWidth(50);
    tf_y.setMaxWidth(50);
    hb_location.getChildren().add(tf_x);
    hb_location.getChildren().add(tf_y);
    grid.add(hb_location, 1, gridy++);

    // Size line
    
    final HBox hb_size = new HBox();
    grid.add(new Label("Size (w,h)"), 0, gridy);
    tf_w.setMaxWidth(50);
    tf_h.setMaxWidth(50);
    hb_size.getChildren().add(tf_w);
    hb_size.getChildren().add(tf_h);
    grid.add(hb_size, 1, gridy++);
    
    // Ok/Cancel
    
    final HBox hb_done = new HBox();
    final Button b_ok = new Button("OK");
    final Button b_cancel = new Button("Cancel");
    hb_done.getChildren().addAll(b_ok, b_cancel);
    
    b_cancel.setOnAction(evt -> hide());
    b_ok.setOnAction(evt -> {
      parent.conf.screen_x = Integer.parseInt(tf_x.getText());
      parent.conf.screen_y = Integer.parseInt(tf_y.getText());
      parent.conf.screen_w = Integer.parseInt(tf_w.getText());
      parent.conf.screen_h = Integer.parseInt(tf_h.getText());
      parent.conf.saveCurrentConfig();
      hide();
    });
    grid.add(hb_done,  1, gridy++);
    
    // Final setup
    Scene configScene = new Scene(grid, 320,200);
    setScene(configScene);
  }
  
  public void showConf() {
    tf_x.setText(String.valueOf(parent.conf.screen_x));
    tf_y.setText(String.valueOf(parent.conf.screen_y));
    tf_w.setText(String.valueOf(parent.conf.screen_w));
    tf_h.setText(String.valueOf(parent.conf.screen_h));
    show(); 
  }

}
