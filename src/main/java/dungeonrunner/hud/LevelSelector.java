package dungeonrunner.hud;

import dungeonrunner.Constants;
import dungeonrunner.DungeonRunner;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LevelSelector extends Group {

    public LevelSelector(double width, double height, Stage stage, DungeonRunner game) {
        Rectangle background = new Rectangle(0, 0, width, height);
        background.setFill(Color.web("#3b6ea5"));
        this.getChildren().add(background);

        int[][][] maps = {
                dungeonrunner.MapSelector.TEST,
                dungeonrunner.MapSelector.LEVEL1,
                dungeonrunner.MapSelector.LEVEL2,
                dungeonrunner.MapSelector.LEVEL3
        };
        String[] names = { "TEST", "LEVEL 1", "LEVEL 2", "LEVEL 3" };

        double margin = 30;
        double squareSize = width / maps.length - margin;
        double squareY = height / 2.0 - squareSize / 2.0;

        for (int i = 0; i < maps.length; i++) {
            final int[][] chosenMap = maps[i];
            double squareX = i * (width / maps.length) + margin / 2.0;

            Rectangle square = new Rectangle(squareX, squareY, squareSize, squareSize);
            square.setFill(Color.WHITE);
            square.setStroke(Color.web("#22334455"));
            square.setStrokeWidth(2);

            Label label = new Label(names[i]);
            label.setLayoutX(squareX);
            label.setLayoutY(squareY + squareSize * 0.25 - 10);
            label.setPrefWidth(squareSize);
            label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-alignment: center;");

            Button btn = new Button("Izaberi");
            btn.setPrefWidth(100);
            btn.setLayoutX(squareX + squareSize / 2.0 - 50);
            btn.setLayoutY(squareY + squareSize * 0.65);

            btn.setOnAction(e -> {
                Constants.MAP = chosenMap;
                game.startGame(stage);
            });

            this.getChildren().addAll(square, label, btn);
        }
    }
}