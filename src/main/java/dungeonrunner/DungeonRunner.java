package dungeonrunner;

import dungeonrunner.enemies.CircularSaw;
import dungeonrunner.enemies.FloorSpike;
import dungeonrunner.tiles.Pillar;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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

    private void buildDungeon ( ) {
        PhongMaterial wallMaterial = new PhongMaterial ( );
        wallMaterial.setDiffuseMap(new Image(DungeonRunner.class.getResourceAsStream("bricks.jpg")));
        wallMaterial.setSpecularColor ( Constants.WALL_SPECULAR_COLOR );

        PhongMaterial exitMaterial = new PhongMaterial();
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

                    wall.setMaterial ( tile == Constants.EXIT ? exitMaterial : wallMaterial );

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
                }
            }
        }
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
        this.player = new Player ( Constants.PLAYER_START_X, Constants.PLAYER_START_Y );
        this.world = new Group ( );

        buildDungeon ( );
        setupLighting ( );
        setupCamera ( );

        Scene scene = new Scene (
                this.world,
                Constants.SCREEN_WIDTH,
                Constants.SCREEN_HEIGHT,
                true,
                SceneAntialiasing.BALANCED
        );
        scene.setCamera ( this.camera );

        setupInput ( scene );

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
                        System.out.println("Player hit by saw!");
                        player.restartPosition();
                    }
                }

                // Register hits from spikes
                for(FloorSpike spike : DungeonRunner.this.spikes) {
                    spike.update(t);
                    if(spike.hitsPlayer(player)) {
                        System.out.println("Player hit by spike!");
                        player.restartPosition();
                    }
                }

                if ( player.isAtExit ( map ) ) {
                    timer.stop ( );
                }
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