import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

import static javafx.geometry.Pos.BOTTOM_CENTER;
import static javafx.geometry.Pos.CENTER;

public class AppVideo extends Application {

    private MediaView mediaView;
    private Slider seekSlider;
    private TextField time;
    private MediaPlayer mediaPlayer;

    public AppVideo() {
        seekSlider = new Slider();
        mediaView = new MediaView();
        time = new TextField();
        time.setPrefWidth(100);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Video");

        final ArrayList<String> fileOpened = new ArrayList<>();

        //Create menu
        Menu fileMenu = new Menu("Файл");
        MenuItem openFile = new MenuItem("Відкрити...");
        fileMenu.getItems().add(openFile);
        openFile.setOnAction((ActionEvent e) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Відкрий мене");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Some video", "*.mp4"),
                    new FileChooser.ExtensionFilter("Всі файли", "*.*")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                System.out.println(file.toURI().toString());
                fileOpened.add(file.toURI().toString());
                Media media = new Media(fileOpened.get(fileOpened.size()-1));
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
                DoubleProperty width = mediaView.fitWidthProperty();
                DoubleProperty height = mediaView.fitHeightProperty();
                width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
                height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    seekSlider.setValue(newValue.toSeconds());
                    time.setText(String.valueOf(newValue.toSeconds()));
                });

                mediaPlayer.setOnReady(() -> seekSlider.setMax(media.getDuration().toSeconds()));
                seekSlider.setOnMouseClicked(event -> mediaPlayer.seek(Duration.seconds(seekSlider.getValue())));
                mediaPlayer.play();

            }
            System.out.println("open");
        });
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu);


        //Create media
        StackPane stackPane = new StackPane();
        stackPane.setAlignment(BOTTOM_CENTER);
        stackPane.getChildren().addAll(mediaView, seekSlider);

        //Create control
        VBox vBox = new VBox();
        vBox.setAlignment(CENTER);
        HBox controls = new HBox();
        controls.setAlignment(CENTER);
        controls.setPadding(new Insets(10, 10, 10, 10));
        controls.setSpacing(10);
        Button playButton;
        playButton = new Button("Play");
        playButton.setOnAction(e-> mediaPlayer.play());

        Button pauseButton = new Button("Пауза");
        pauseButton.setOnAction(e-> mediaPlayer.pause());

        Slider volume = new Slider();
        volume.valueProperty().addListener(observable -> mediaPlayer.setVolume(volume.getValue()/100));
        controls.getChildren().addAll(playButton, pauseButton, volume, time);
        vBox.getChildren().add(controls);

        BorderPane layout = new BorderPane();
        layout.setTop(menuBar);
        layout.setCenter(stackPane);
        layout.setBottom(vBox);
        Scene scene = new Scene(layout, 400, 300);
        scene.setOnMouseClicked(e->{
            if (e.getClickCount() == 2)
                primaryStage.setFullScreen(true);
        });
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
