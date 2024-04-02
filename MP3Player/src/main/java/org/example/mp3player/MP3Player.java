package org.example.mp3player;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import javax.xml.transform.Source;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class MP3Player implements Initializable {

    @FXML
    private Pane rootPane;
    @FXML
    private Button playButton,previousButton,nextButton;
    @FXML
    private Label songLabel;
    @FXML
    private ImageView songImage;
    @FXML
    private ImageView pausePlayImage;
    @FXML
    private Label totalTime;
    private Image pause,play;



    private ArrayList<File> songs;
    private File directory;
    private File[] files;
    private int songNumber;
    private boolean running;
    private Duration duration;
    private Media media;
    private MediaPlayer mediaPlayer;
    private String songName;
    //making a progress bar which updates itself with the song
    private static final int line_Length=250;
    private static final int circle_Radius=5;
    private Timeline timeline;
    private double currentTime=0;
    @FXML
    private Label timeLabel;
    private Line progressbar;
    private Circle indicator;
    private TranslateTransition translateRight,translateLeft;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        songs = new ArrayList<File>();
        directory= new File("src/main/resources/music");
        files=directory.listFiles();
        if (files!=null){
            songs.addAll(Arrays.asList(files));
        }

                 media=new Media(songs.get(songNumber).toURI().toString());
                 mediaPlayer=new MediaPlayer(media);

                 songName=songs.get(songNumber).getName();
                 songLabel.setText(songName.substring(0,songName.length()-4));

                 progressbar=new Line(70,369,line_Length+70,369);
                 indicator=new Circle(70,369,circle_Radius);
                 progressbar.setStrokeWidth(3);
                 progressbar.setStroke(Paint.valueOf(String.valueOf(Color.WHITE)));
                 indicator.setFill(Paint.valueOf(String.valueOf(Color.WHITE)));

                     rootPane.getChildren().add(progressbar);
                     rootPane.getChildren().add(indicator);

                  timeLabel.setText("0:00");
                  timeLabel.setLayoutX(30);
                  timeLabel.setLayoutY(360);
                  songLabel.setLayoutX(10);
                  songLabel.setLayoutY(300);
                  timeLabel.setTextFill(Paint.valueOf(String.valueOf(Color.WHITE)));


                  pause=new Image("/img_3.png");
                  play=new Image("/img_2.png");






                 timeline=new Timeline();
                 timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1)));
                 timeline.setCycleCount(Timeline.INDEFINITE);

                 translateLeft=new TranslateTransition(Duration.seconds(6),songLabel);
                 translateLeft.setFromX(0);
                 translateLeft.setToX(-300);
                 translateLeft.setInterpolator(Interpolator.LINEAR);
                 translateLeft.setCycleCount(1);


                 translateRight=new TranslateTransition(Duration.seconds(6),songLabel);
                 translateRight.setFromX(300);
                 translateRight.setToX(0);
                 translateRight.setInterpolator(Interpolator.LINEAR);
                 translateRight.setCycleCount(1);


                 translateRight.setOnFinished(event->{
                     translateLeft.setDelay(Duration.millis(1000));
                     translateLeft.play();
                 });
                 translateLeft.setOnFinished(event -> {
                     translateRight.play();
                 });
                 translateLeft.setDelay(Duration.millis(1000));
                 translateLeft.play();


               mediaPlayer.setOnReady(()->{
                   duration=mediaPlayer.getTotalDuration();
                   int minutes=(int) duration.toSeconds()/60;
                   int seconds=(int) duration.toSeconds()%60;
                   totalTime.setText(String.format("%d:%02d",minutes,seconds));
               });

               indicator.setOnMouseDragged(event -> {
                   double mouseX=event.getX();
                   double newX=Math.max(progressbar.getStartX(),Math.min(progressbar.getEndX(),mouseX));
                   indicator.setCenterX(newX);

                   double speed=line_Length/duration.toSeconds();
                   double newTime=(newX-progressbar.getStartX())/speed;
                   int minutes=(int) newTime/60;
                   int seconds=(int) newTime%60;
                   timeLabel.setText(String.format("%d:%02d",minutes,seconds));
                   mediaPlayer.seek(Duration.seconds(newTime));


                   event.consume();
               });
               progressbar.setOnMousePressed(event -> {
                   double mouseX=event.getX();
                   double newX=Math.max(progressbar.getStartX(),Math.min(progressbar.getEndX(),mouseX));
                   indicator.setCenterX(newX);

                   double speed=line_Length/duration.toSeconds();
                   double newTime=(newX-progressbar.getStartX())/speed;
                   int minutes=(int) newTime/60;
                   int seconds=(int) newTime%60;
                   timeLabel.setText(String.format("%d:%02d",minutes,seconds));
                   mediaPlayer.seek(Duration.seconds(newTime));


                   event.consume();
               });




    }
    public void playPauseMedia(){
        timeline.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                currentTime=mediaPlayer.getCurrentTime().toSeconds();
                updateIndicator();
                updateTimeLabel();
            }
        });

        if (running){

            pausePlayImage.setImage(play);
            mediaPlayer.pause();
            running=false;
            timeline.stop();

        }
        else{
            pausePlayImage.setImage(pause);
            mediaPlayer.play();
            running=true;
            timeline.play();

        }


    }
    public void previousMedia(){

        if (songNumber>0){

            songNumber--;
            mediaPlayer.stop();
            media=new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer=new MediaPlayer(media);

            songName=songs.get(songNumber).getName();
            songLabel.setText(songName.substring(0,songName.length()-4));
            running=false;
            playPauseMedia();

        }
        else {

            songNumber=songs.size()-1;
            mediaPlayer.stop();
            media=new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer=new MediaPlayer(media);

            songName=songs.get(songNumber).getName();
            songLabel.setText(songName.substring(0,songName.length()-4));
            running=false;
            playPauseMedia();
        }
    }

    public void changeTime(double currentTime){
        mediaPlayer.seek(Duration.seconds(currentTime));
    }
    public void nextMedia(){
        if (songNumber<songs.size()-1){
            songNumber++;
            mediaPlayer.stop();
            media=new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer=new MediaPlayer(media);

            songName=songs.get(songNumber).getName();
            songLabel.setText(songName.substring(0,songName.length()-4));
            running=false;
            playPauseMedia();

        }
        else {
            songNumber=0;
            mediaPlayer.stop();
            media=new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer=new MediaPlayer(media);

            songName=songs.get(songNumber).getName();
            songLabel.setText(songName.substring(0,songName.length()-4));
            running=false;
            playPauseMedia();
        }
    }
     private void updateIndicator(){
         double progress = currentTime / duration.toSeconds();


         double x = ((progress * line_Length) + progressbar.getStartX());

         indicator.setCenterX(x);
         if (progress==1){
             playPauseMedia();
         }
     }
     private void updateTimeLabel(){
        int minutes=(int) currentTime/60;
        int seconds=(int) currentTime%60;
        timeLabel.setText(String.format("%d:%02d",minutes,seconds));
     }



}
