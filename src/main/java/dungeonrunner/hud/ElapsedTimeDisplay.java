package dungeonrunner.hud;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ElapsedTimeDisplay {
    private static final double FONT_SIZE = 22.0;
    private static final double STROKE_WIDTH = 1.0;

    private final Text label;
    private double startedAT = -1.0;
    private int lastShownSeconds = -1;

    public ElapsedTimeDisplay(){
        this.label = new Text("Time elapsed: 00:00");
        this.label.setFont(Font.font("Arial", FontWeight.BOLD, FONT_SIZE));
        this.label.setFill(Color.WHITE);
        this.label.setStroke(Color.rgb(0, 0, 0, 0.6));
        this.label.setStrokeWidth(STROKE_WIDTH);
    }

    public Text getNode() { return label; }

    public void update(double t){
        if(startedAT < 0) startedAT = t;

        int totalSeconds = (int) (t - startedAT);
        if(totalSeconds == lastShownSeconds) return;
        lastShownSeconds = totalSeconds;

        int minutes = totalSeconds/60;
        int seconds = totalSeconds%60;
        label.setText("Time elapsed: " + formatTime(minutes, seconds));
    }

    public void stop() {
        startedAT = -1.0;
        lastShownSeconds = -1;
        label.setText("00:00");
    }

    private String formatTime(int minutes, int seconds){
        return String.format("%02d:%02d", minutes, seconds);
    }
}
