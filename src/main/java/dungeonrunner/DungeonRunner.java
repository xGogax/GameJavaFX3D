package dungeonrunner;

import dungeonrunner.enemies.CircularSaw;
import dungeonrunner.enemies.FloorSpike;
import dungeonrunner.hud.ElapsedTimeDisplay;
import dungeonrunner.hud.HealthIndicator;
import dungeonrunner.hud.LevelSelector;
import dungeonrunner.hud.Minimap;
import dungeonrunner.items.Key;
import dungeonrunner.tiles.Pillar;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DungeonRunner extends Application {

    private DungeonMap map;
    private Player player;
    private Group world;
    private PerspectiveCamera camera;
    private Group cameraMount;
    private PointLight torch;
    private AnimationTimer timer;

    private List<CircularSaw> saws;
    private List<FloorSpike> spikes;

    private HealthIndicator healthIndicator;

    private StackPane overlay;
    private Text overlayText;

    private Key key;
    private Box exitBox;
    private PhongMaterial exitMaterial;

    private Minimap minimap;

    private ElapsedTimeDisplay elapsedTimeDisplay;

    private void buildDungeon ( ) {
        PhongMaterial wallMaterial = new PhongMaterial ( );
        wallMaterial.setDiffuseMap(new Image(DungeonRunner.class.getResourceAsStream("bricks.jpg")));
        wallMaterial.setSpecularColor ( Constants.WALL_SPECULAR_COLOR );

        exitMaterial = new PhongMaterial();
        exitMaterial.setDiffuseColor ( Constants.EXIT_DIFFUSE_COLOR );
        exitMaterial.setSpecularColor ( Constants.EXIT_SPECULAR_COLOR );

        PhongMaterial floorMaterial = new PhongMaterial();
        floorMaterial.setDiffuseColor(Color.rgb(60, 40, 20));

        PhongMaterial ceilingMaterial = new PhongMaterial();
        ceilingMaterial.setDiffuseColor(Color.rgb(25, 25, 45));
        
        this.map = new DungeonMap ( Constants.MAP );
        this.saws = new ArrayList<>();
        this.spikes = new ArrayList<>();

        int    rows       = this.map.getRows ( );
        int    columns    = this.map.getCols ( );
        double totalWidth = columns * Constants.CELL_SIZE;
        double totalDepth = rows * Constants.CELL_SIZE;

        Box floor = new Box ( totalWidth, Constants.SLAB_THICKNESS, totalDepth );
        Translate floorTranslate = new Translate (
                totalWidth / 2.0,
                Constants.WALL_HEIGHT / 2.0 + Constants.SLAB_THICKNESS / 2.0,
                totalDepth / 2.0
        );
        floor.getTransforms ( ).add ( floorTranslate );
        floor.setMaterial ( floorMaterial );

        Box ceiling = new Box ( totalWidth, Constants.SLAB_THICKNESS, totalDepth );
        Translate ceilingTranslate = new Translate (
                totalWidth / 2.0,
                -Constants.WALL_HEIGHT / 2.0 - Constants.SLAB_THICKNESS / 2.0,
                totalDepth / 2.0
        );
        ceiling.getTransforms ( ).add ( ceilingTranslate );
        ceiling.setMaterial ( ceilingMaterial );

        this.world.getChildren ( ).addAll ( floor, ceiling );

        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {
                int tile = this.map.get ( column, row );
                if ( tile == Constants.WALL || tile == Constants.EXIT ) {
                    Box wall = new Box ( Constants.CELL_SIZE, Constants.WALL_HEIGHT, Constants.CELL_SIZE );

                    Translate wallTranslate = new Translate (
                            column * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0,
                            0,
                            row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0
                    );
                    wall.getTransforms ( ).add ( wallTranslate );
                    wall.setMaterial(wallMaterial);
                    if(tile == Constants.EXIT) {
                        this.exitBox = wall;
                    }

                    this.world.getChildren().add ( wall );
                } else if (tile == Constants.PILLAR) {
                    this.world.getChildren().add(new Pillar(column, row, wallMaterial).getPillarMesh());
                } else if (tile == Constants.SAW) {
                    double ph = (double)(this.saws.size() / 2) * 0.35;
                    CircularSaw saw1 = new CircularSaw(this.map, column, row, ph, false);
                    CircularSaw saw2 = new CircularSaw(this.map, column, row, ph, true);
                    this.saws.add(saw1);
                    this.saws.add(saw2);
                    this.world.getChildren().addAll(new Node[]{saw1.getNode(), saw2.getNode()});
                } else if (tile == Constants.SPIKE) {
                    double cx = (double) (column * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0);
                    double cz = (double) (row * Constants.CELL_SIZE + Constants.CELL_SIZE / 2.0);
                    FloorSpike fs = new FloorSpike(cx, cz, (double)this.spikes.size() * 0.4);
                    this.spikes.add(fs);
                    this.world.getChildren().add(fs.getNode());
                } else if (tile == Constants.KEY) {
                    this.key = new Key(column, row);
                    this.world.getChildren().add(this.key.getNode());
                }
            }
        }
    }

    private void showGameOverOverlay(String message, Color textColor) {
        this.overlayText.setText(message);
        this.overlayText.setFill(textColor);
        this.overlay.setVisible(true);
    }

    private void setupLighting ( ) {
        AmbientLight ambient = new AmbientLight ( Constants.AMBIENT_LIGHT_COLOR );

        this.torch = new PointLight ( Constants.POINT_LIGHT_COLOR );
        this.torch.setMaxRange ( Constants.CELL_SIZE * 6 );

        this.world.getChildren ( ).addAll ( ambient, torch );
    }

    private void setupCamera ( ) {
        this.camera = new PerspectiveCamera ( true );

        this.camera.setNearClip ( Constants.CAMERA_NEAR_CLIP );
        this.camera.setFarClip ( Constants.CAMERA_FAR_CLIP );
        this.camera.setFieldOfView ( Constants.CAMERA_FIELD_OF_VIEW );

        this.cameraMount = new Group ( this.camera );

        this.world.getChildren ( ).add ( cameraMount );

        updateCameraMount ( );
    }

    private void setupInput ( Scene scene ) {
        scene.setOnKeyPressed ( event -> {
            switch ( event.getCode ( ) ) {
                case UP: {
                    this.player.setMoveForward ( true );
                    break;
                }
                case DOWN: {
                    this.player.setMoveBackward ( true );
                    break;
                }
                case LEFT: {
                    this.player.setRotateLeft ( true );
                    break;
                }
                case RIGHT: {
                    this.player.setRotateRight ( true );
                    break;
                }
            }
        } );

        scene.setOnKeyReleased ( event -> {
            switch ( event.getCode ( ) ) {
                case UP: {
                    this.player.setMoveForward ( false );
                    break;
                }
                case DOWN: {
                    this.player.setMoveBackward ( false );
                    break;
                }
                case LEFT: {
                    this.player.setRotateLeft ( false );
                    break;
                }
                case RIGHT: {
                    this.player.setRotateRight ( false );
                    break;
                }
            }
        } );
    }

    private void updateCameraMount ( ) {
        Translate camerMountTranslate = new Translate (
                this.player.getPositionX( ) * Constants.CELL_SIZE,
                0,
                this.player.getPositionY( ) * Constants.CELL_SIZE
        );

        Rotate camerMountRotate = new Rotate (
                Math.toDegrees ( Math.atan2 ( this.player.getDirectionX ( ), this.player.getDirectionY ( ) ) ),
                Rotate.Y_AXIS
        );

        this.cameraMount.getTransforms ( ).setAll (
                camerMountTranslate,
                camerMountRotate
        );
    }

    private void updateTorch() {
        Translate torchTranslate = new Translate (
                this.player.getPositionX( ) * Constants.CELL_SIZE,
                0,
                this.player.getPositionY( ) * Constants.CELL_SIZE
        );

        this.torch.getTransforms ( ).setAll ( torchTranslate );
    }

    @Override
    public void start ( Stage stage ) {
        LevelSelector selector = new LevelSelector(
                Constants.SCREEN_WIDTH,
                Constants.SCREEN_HEIGHT,
                stage,
                this
        );
        Scene menuScene = new Scene(selector, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        stage.setTitle("Izaberi nivo");
        stage.setScene(menuScene);
        stage.setResizable(false);
        stage.show();
    }

    public void startGame(Stage stage) {
        this.player = new Player ( Constants.PLAYER_START_X, Constants.PLAYER_START_Y );
        this.world = new Group ( );

        buildDungeon ( );
        setupLighting ( );
        setupCamera ( );

        SubScene sub3D = new SubScene (
                this.world,
                Constants.SCREEN_WIDTH,
                Constants.SCREEN_HEIGHT,
                true,
                SceneAntialiasing.BALANCED
        );
        sub3D.setCamera ( this.camera );

        this.minimap = new Minimap(this.map);

        this.healthIndicator = new HealthIndicator(this.player.getHealth());

        this.elapsedTimeDisplay = new ElapsedTimeDisplay();

        // GAME OVER / VICTORY OVERLAY
        this.overlayText = new Text();
        this.overlayText.setFont(Font.font("Arial", FontWeight.BOLD, 52.0));
        Rectangle backdrop = new Rectangle(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Color.rgb(0, 0, 0, 0.65));
        this.overlay = new StackPane(backdrop, this.overlayText);
        this.overlay.setVisible(false);

        // pravljenje HUD-a
        StackPane root = new StackPane(sub3D, this.healthIndicator.getNode(), this.minimap.getNode(),this.elapsedTimeDisplay.getNode() ,this.overlay);
        StackPane.setAlignment(this.healthIndicator.getNode(), Pos.TOP_LEFT);
        StackPane.setAlignment(this.minimap.getNode(), Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(this.elapsedTimeDisplay.getNode(), Pos.TOP_RIGHT);

        Scene scene = new Scene (root, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT );

        setupInput(scene);

        if(key == null) {
            player.setHasKey();
            exitBox.setMaterial(exitMaterial);
        }
        this.timer = new AnimationTimer ( ) {
            @Override
            public void handle ( long now ) {
                player.update (map);

                updateCameraMount ( );

                double t = (double)now * 1.0E-9;
                double f = (double)0.75F + (double)0.25F * (Math.sin(t * 7.3) * (double)0.5F + Math.sin(t * 13.7) * 0.3 + Math.sin(t * 19.1) * 0.2);
                DungeonRunner.this.torch.setColor(Constants.POINT_LIGHT_COLOR.deriveColor((double)0.0F, (double)1.0F, f, (double)1.0F));
                updateTorch ( );

                // Register hits from saws
                for(CircularSaw saw : DungeonRunner.this.saws) {
                    saw.update(t);
                    if(saw.hitsPlayer(player)) {
                        player.takeHit();
                        healthIndicator.update(player.getHealth());
                        System.out.println("Player hit by saw! (Life: " + player.getHealth() + ")");
                        player.restartPosition();
                    }
                }

                // Register hits from spikes
                for(FloorSpike spike : DungeonRunner.this.spikes) {
                    spike.update(t);
                    if(spike.hitsPlayer(player)) {
                        player.takeHit();
                        healthIndicator.update(player.getHealth());
                        System.out.println("Player hit by spike! (Life: " + player.getHealth() + ")");
                        player.restartPosition();
                    }
                }

                // Register key coll
                if (key != null) {
                    key.update(t);
                    if (key.tryCollect(player)) {
                        player.setHasKey();
                        if (exitBox != null) {
                            exitBox.setMaterial(exitMaterial);
                        }
                    }
                }

                if (player.getHealth() == 0) {
                    System.out.println("Game Over! Player has no more lives.");
                    timer.stop();
                    showGameOverOverlay("Izgubili ste!", Color.DARKRED);
                }

                if (player.isAtExit(map) && (key == null || key.isCollected())) {
                    System.out.println("Congratulations! You've reached the exit.");
                    timer.stop();
                    showGameOverOverlay("Pobegli ste!", Color.GOLD);
                }

                elapsedTimeDisplay.update(t);
                minimap.update(player, key);
            }
        };
        timer.start ( );

        stage.setTitle ( "Beg iz tamnice" );
        stage.setScene ( scene );
        stage.setResizable ( false );
        stage.show ( );
    }

    public static void main ( String[] args ) {
        launch ( args );
    }
}