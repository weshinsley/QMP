package com.teapotrecords.qmp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class QMP extends Application {
  
  public String getVersion() { return "0.2"; }
  public String getVersionDate() { return "30th Oct 2020"; }
  
  // Configuration
  
  Config conf;
  ConfigScreen config_screen;
  Movie movie_player;
  
  // The list of movies we can play
  
  ObservableList<String> ol_movies = FXCollections.observableArrayList();
  ArrayList<String> full_paths = new ArrayList<String>();
  ListView<String> lv_movies = new ListView<String>(ol_movies);
  ScrollPane sp_movies = new ScrollPane(lv_movies);
  
  Scene mainScene = null;
  MediaPlayer player;
  boolean is_paused;
  
  private Button b_play_pause;
  private Button b_rewind;
  private Button b_stop = new Button("O");
  private Button b_add = new Button("+");
  private Button b_up = new Button("^");
  private Button b_down = new Button("v");
  private Button b_del = new Button("X");
  
  private void startMovie() {
    is_paused = false;
    int selected = lv_movies.getSelectionModel().getSelectedIndex();
    b_play_pause.setText("||");
    b_stop.setDisable(false);
    b_rewind.setDisable(true);
    movie_player.play(full_paths.get(selected));
  }
  
  protected void endMovie() {
    b_play_pause.setText(">");
    b_stop.setDisable(true);
    is_paused = false;
    player.stop();
    movie_player.hide();
  }
  
  protected void delete(int selected) {
    full_paths.remove(selected);
    ol_movies.remove(selected);
    selected = lv_movies.getSelectionModel().getSelectedIndex();
    updateButtons(selected);
    conf.saveCurrentConfig();
  }
  
  private File saveDialog(Stage stage) {
    FileChooser fc = new FileChooser();
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("QMP Config(*.xml)", "*.xml"));
    return fc.showSaveDialog(stage);
  }
  
  private void player_exit() {
    if (movie_player.is_playing()) {
      player.stop();
      movie_player.hide();
    }
    System.exit(0);
  }
  
  private void updateButtons(int selected) {
    b_up.setDisable(selected <= 0);
    b_down.setDisable(selected == -1 || selected == full_paths.size()-1);
    b_del.setDisable(selected == -1);
    b_play_pause.setDisable(selected == -1);
    b_rewind.setDisable(!movie_player.is_playing());
    b_stop.setDisable(!movie_player.is_playing());
  }
  
  private void initUI(Stage stage) {
    VBox root = new VBox();
    root.setPadding(new Insets(10));
    
    // Main menu
    
    MenuBar mb_main = new MenuBar();
    Menu m_file = new Menu("File");
    MenuItem mi_newconfig = new MenuItem("New Config");
    MenuItem mi_loadconfig = new MenuItem("Load Config");
    MenuItem mi_saveconfig = new MenuItem("Save Config As");
    MenuItem mi_exit = new MenuItem("Exit");
    m_file.getItems().addAll(mi_newconfig, mi_loadconfig, mi_saveconfig, mi_exit);
    
    // File New
    mi_newconfig.setOnAction(evt -> {
      File f = saveDialog(stage);
      if (f != null) {
        ol_movies.clear();
        full_paths.clear();
        conf.current_conf = f.getAbsolutePath();
        conf.saveCurrentConfig();
        conf.saveQMPConfig();
      }
    });
    
    // File Load
    mi_loadconfig.setOnAction(evt -> {
      FileChooser fc = new FileChooser();
      fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("QMP Config(*.xml)", "*.xml"));
      File f = fc.showOpenDialog(stage);
      if (f != null) {
        conf.current_conf = f.getAbsolutePath();
        conf.loadCurrentConfig();
        stage.setTitle("QMP - "+conf.current_conf_short);
        conf.saveQMPConfig();
      }
    });
    
    // File Save As
    mi_saveconfig.setOnAction(evt -> {
      File f = saveDialog(stage);
      if (f != null) {
        conf.current_conf = f.getAbsolutePath();
        conf.saveCurrentConfig();
        stage.setTitle("QMP - "+conf.current_conf_short);
        conf.saveQMPConfig();
      }
    });
    
    // File Exit
    mi_exit.setOnAction(evt -> {
      player_exit();
    });
        
    // Settings, Screen
    
    Menu m_settings = new Menu("Settings");
    MenuItem mi_screen = new MenuItem("Screen settings");
    mi_screen.setOnAction(evt -> config_screen.showConf());
    m_settings.getItems().add(mi_screen);
    
    // Help About
    Menu m_help = new Menu("Help");
    MenuItem mi_about = new MenuItem("About");
    Alert about_dialog = new Alert(AlertType.INFORMATION);
    about_dialog.setTitle("About Quick Media Player "+getVersion());
    about_dialog.setHeaderText("Issues or Comments: https://github.com/weshinsley/QMP");
    about_dialog.setContentText("wes.hinsley@gmail.com - "+getVersionDate());
    mi_about.setOnAction(evt -> about_dialog.showAndWait());
    m_help.getItems().add(mi_about);
    mb_main.getMenus().addAll(m_file, m_settings, m_help);
    
    VBox vb_menu = new VBox(mb_main);
    root.getChildren().add(vb_menu);
    
    // The list of things to play...
    
    root.getChildren().add(sp_movies);
    sp_movies.setVbarPolicy(ScrollBarPolicy.ALWAYS);
    sp_movies.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
    
    // Allow drag-drop files into lv_movies
    
    lv_movies.setOnDragOver(evt -> {
      evt.acceptTransferModes(TransferMode.LINK);
    });
    
    lv_movies.setOnDragDropped(evt -> {
      List<File> files = evt.getDragboard().getFiles();
      for (int i=0; i<files.size(); i++) {
        String f = files.get(i).getName().toLowerCase();
        if (f.endsWith(".avi") || (f.endsWith(".mp4")) || (f.endsWith(".mov"))) {
          ol_movies.add(files.get(i).getName());
          full_paths.add(files.get(i).getAbsolutePath());
        }
      }
      conf.saveCurrentConfig();
    });
    
      
    // A toolbar with 
    // Media control - two buttons:
    //   (1) Play / Pause
    //   (2) Rewind
    // List control 
    //   up/down/delete/add buttons
    
    HBox hb_buttons = new HBox();
    hb_buttons.setAlignment(Pos.CENTER_RIGHT);
    b_up.setDisable(true);
    b_down.setDisable(true);
    b_del.setDisable(true);
    
    hb_buttons.getChildren().addAll(b_add, b_up, b_down, b_del);
    
    // Move up/down events
    
    b_up.setOnAction(evt -> {
      int selected = lv_movies.getSelectionModel().getSelectedIndex();
      String temp = full_paths.get(selected);
      full_paths.set(selected, full_paths.get(selected-1));
      full_paths.set(selected - 1,  temp);
      temp = ol_movies.get(selected);
      ol_movies.set(selected, ol_movies.get(selected - 1));
      ol_movies.set(selected - 1, temp);
      selected--;
      lv_movies.getSelectionModel().select(selected);
      updateButtons(selected);
      conf.saveCurrentConfig();
    });
    
    b_down.setOnAction(evt -> {
      int selected = lv_movies.getSelectionModel().getSelectedIndex();
      String temp = full_paths.get(selected);
      full_paths.set(selected, full_paths.get(selected + 1));
      full_paths.set(selected + 1,  temp);
      temp = ol_movies.get(selected);
      ol_movies.set(selected, ol_movies.get(selected + 1));
      ol_movies.set(selected + 1, temp);
      selected++;
      lv_movies.getSelectionModel().select(selected);
      updateButtons(selected);
      conf.saveCurrentConfig();
    });
    
    b_del.setOnAction(evt -> {
      delete(lv_movies.getSelectionModel().getSelectedIndex());
      
    });
    
    HBox hb_media_buttons = new HBox();
    b_play_pause = new Button(">");
    b_rewind = new Button("<<");
    hb_media_buttons.getChildren().addAll(b_rewind, b_play_pause, b_stop);
    hb_media_buttons.setAlignment(Pos.CENTER_LEFT);
    b_rewind.setDisable(true);
    b_play_pause.setDisable(true);
    b_stop.setDisable(true);
    
    b_play_pause.setOnAction(evt -> {
      if (movie_player.is_playing()) {
        if (!is_paused) {
          player.pause();
          is_paused = true;
          b_rewind.setDisable(false);
          b_play_pause.setText(">");
        } else {
          is_paused = false;
          b_play_pause.setText("||");
          player.play();
        }
      } else {
        startMovie();
        b_stop.setDisable(false);
      }
    });
    
    b_stop.setOnAction(evt -> {
      endMovie();
    });
    
    b_rewind.setOnAction(evt -> {
      player.seek(player.getStartTime());
    });
    
    // Add movies using add button and browser...
    b_add.setOnAction(evt -> {
      FileChooser fc = new FileChooser();
      fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Movies", "*.avi", "*.mov", "*.mp4"));
      List<File> fs = fc.showOpenMultipleDialog(stage);
      if (fs!=null) {
        for (int i = 0; i < fs.size(); i++) {
          full_paths.add(fs.get(i).getAbsolutePath());
          ol_movies.add(fs.get(i).getName());
        }
        conf.saveCurrentConfig();
        updateButtons(lv_movies.getSelectionModel().getSelectedIndex());
      }
    });
    
    AnchorPane ap = new AnchorPane();
    ap.getChildren().addAll(hb_buttons, hb_media_buttons);
    AnchorPane.setRightAnchor(hb_buttons,  0.0);
    AnchorPane.setLeftAnchor(hb_media_buttons,  0.0);
    root.getChildren().add(ap);
    
    // Click on list element
    
    lv_movies.setOnMouseClicked(evt -> {
      int clicks = evt.getClickCount();
      int selected = lv_movies.getSelectionModel().getSelectedIndex();
      updateButtons(selected);
      if (clicks > 1) {
        if (movie_player.is_playing()) {
          player.stop();
          movie_player.hide();
          is_paused = false;
        }
        startMovie();
      }
    });
    
    lv_movies.setOnKeyReleased(evt -> {
      if ((evt.getCode() == KeyCode.DOWN) || (evt.getCode() == KeyCode.UP)) {
        updateButtons(lv_movies.getSelectionModel().getSelectedIndex());
      } else if ((evt.getCode() == KeyCode.ENTER) || (evt.getCode() == KeyCode.SPACE)) {
        if (movie_player.is_playing()) {
          player.stop();
          movie_player.hide();
          is_paused = false;
        }
        startMovie();
      } else if ((evt.getCode() == KeyCode.DELETE) || (evt.getCode() == KeyCode.BACK_SPACE)) {
        delete(lv_movies.getSelectionModel().getSelectedIndex());
      }
    });
    
    mainScene = new Scene(root, 280,200);
    stage.setOnCloseRequest(evt -> player_exit());
    stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) stage.setMaximized(false);
    });
    stage.setTitle("QMP - "+conf.current_conf_short);
    stage.setScene(mainScene);
    
    stage.show();
  }
    
  @Override
  public void start(Stage stage) {
    conf = new Config(this);
    config_screen = new ConfigScreen(this);
    movie_player = new Movie(this);
    conf.loadQMPConfig();
    conf.loadCurrentConfig();
    stage.setTitle("QMP - "+conf.current_conf_short);
    initUI(stage);
  }
  
   public static void main(String[] args) {
     launch(args);
   }
}