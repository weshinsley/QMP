package com.teapotrecords.qmp;

import java.io.File;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Movie {
  private QMP parent;
  private boolean _is_playing = false;
  private Stage current_stage = null;
  private Scene current_scene = null;
  private MediaPlayer mp = null;

  public Duration get_time() {
    return mp.getCurrentTime();
  }

  public Duration get_final_time() {
    return mp.getMedia().getDuration();
  }

  public boolean is_playing() {
    return _is_playing;
  }

  public Movie(QMP parent) {
    this.parent = parent;
  }

  public void hide() {
    if (current_stage!=null) {
      if (current_stage.isShowing()) current_stage.hide();
      current_scene = null;
      current_stage = null;
    }
  }

  public void play(String movie) {
    String ext = movie.substring(movie.lastIndexOf('.') + 1).toUpperCase();
	final File f = new File(movie);
    final Media m = new Media(f.toURI().toString());
    mp = new MediaPlayer(m);
    if (!(ext.equals("MP3")) || (ext.equals("WAV"))) {
      final MediaView mv = new MediaView(mp);
      final DoubleProperty width = mv.fitWidthProperty();
      final DoubleProperty height = mv.fitHeightProperty();
      mv.setPreserveRatio(true);
      StackPane root = new StackPane();
      root.getChildren().add(mv);
      current_stage = new Stage();
      current_stage.setX(parent.conf.screen_x);
      current_stage.setY(parent.conf.screen_y);
      current_stage.setWidth(parent.conf.screen_w);
      current_stage.setHeight(parent.conf.screen_h);
      current_scene = new Scene(root, parent.conf.screen_w, parent.conf.screen_h);
      current_scene.setFill(Color.BLACK);
      width.bind(Bindings.selectDouble(mv.sceneProperty(), "width"));
      height.bind(Bindings.selectDouble(mv.sceneProperty(),  "height"));
      current_stage.initStyle(StageStyle.UNDECORATED);
      root.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(0), new Insets(0,0,0,0))));
      current_stage.setScene(current_scene);
      current_stage.show();
    }
    mp.setOnEndOfMedia(new Runnable() {
      @Override public void run() {
        _is_playing = false;
        if (current_stage != null) current_stage.hide();
        current_scene = null;
        current_stage = null;
        parent.endMovie();

      }
    });
    mp.play();
    _is_playing = true;
    parent.player = mp;
  }

}
