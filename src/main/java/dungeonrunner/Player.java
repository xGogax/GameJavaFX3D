package dungeonrunner;

import javafx.geometry.Point2D;
import javafx.scene.transform.Rotate;

public class Player {
    private double positionX;
    private double positionY;
    private double directionX;
    private double directionY;
    private boolean moveForward;
    private boolean moveBackward;
    private boolean rotateLeft;
    private boolean rotateRight;

    private final double startX;
    private final double startY;
    private final double startDirX;
    private final double startDirY;

    private boolean hasKey = false;

    private final int maxHealth;
    private int health = 3;

    private boolean controlsInverted = false;
    private double controlsInvertedUntil;

    public Player ( double startX, double startY ) {
        this.positionX = startX;
        this.positionY = startY;
        this.startX = startX;
        this.startY = startY;
        this.directionX =  1.0;
        this.directionY =  0.0;
        this.startDirX = directionX;
        this.startDirY = directionY;
        this.maxHealth = health;
    }

    public double getPositionX ( ) { return this.positionX; }
    public double getPositionY ( ) { return this.positionY; }
    public double getDirectionX ( ) { return this.directionX; }
    public double getDirectionY ( ) { return this.directionY; }
    public int getHealth() { return this.health; }
    public int getMaxHealth() { return this.maxHealth; }

    public void setMoveForward  ( boolean newValue ) { this.moveForward  = newValue; }
    public void setMoveBackward ( boolean newValue ) { this.moveBackward = newValue; }
    public void setRotateLeft   ( boolean newValue ) { this.rotateLeft   = newValue; }
    public void setRotateRight  ( boolean newValue ) { this.rotateRight  = newValue; }

    public void restartPosition() {
        this.positionX = this.startX;
        this.positionY = this.startY;
        this.directionX = this.startDirX;
        this.directionY = this.startDirY;
    }

    public void gainHealth() { this.health++; }
    public void takeHit() { this.health--; }

    public void setHasKey() { this.hasKey = true; }

    public void applyControlInversion(double currentTime, double durationSeconds) {
        this.controlsInverted = true;
        this.controlsInvertedUntil = currentTime + durationSeconds;
    }

    public boolean isControlsInverted() {
        return controlsInverted;
    }

    public void update ( DungeonMap map ) {
        if (controlsInverted && System.nanoTime() * 1.0E-9 > controlsInvertedUntil) {
            controlsInverted = false;
        }

        boolean forward = moveForward;
        boolean backward = moveBackward;

        if (controlsInverted) {
            forward = moveBackward;
            backward = moveForward;
        }

        if (forward) {
            double newX = this.positionX + this.directionX * Constants.PLAYER_MOVE_SPEED;
            double newY = this.positionY + this.directionY * Constants.PLAYER_MOVE_SPEED;

            if (canMoveTo(newX, positionY, map)) {
                this.positionX = newX;
            }

            if (canMoveTo(positionX, newY, map)) {
                this.positionY = newY;
            }
        }

        if (backward) {
            double newX = this.positionX - this.directionX * Constants.PLAYER_MOVE_SPEED;
            double newY = this.positionY - this.directionY * Constants.PLAYER_MOVE_SPEED;

            if (canMoveTo(newX, positionY, map)) {
                this.positionX = newX;
            }

            if (canMoveTo(positionX, newY, map)) {
                this.positionY = newY;
            }
        }

        boolean left = rotateLeft;
        boolean right = rotateRight;

        if (controlsInverted) {
            left = rotateRight;
            right = rotateLeft;
        }

        if (left) {
            rotate(Constants.PLAYER_ROTATION_SPEED);
        }

        if (right) {
            rotate(-Constants.PLAYER_ROTATION_SPEED);
        }
    }

    public boolean isAtExit ( DungeonMap map ) {
        return map.get ( ( int ) this.positionX, ( int ) this.positionY ) == Constants.EXIT;
    }
    private boolean canMoveTo ( double x, double y, DungeonMap map ) {
        return isFree ( ( int ) ( x + Constants.PLAYER_RADIUS ), ( int ) ( y + Constants.PLAYER_RADIUS ), map )
            && isFree ( ( int ) ( x + Constants.PLAYER_RADIUS ), ( int ) ( y - Constants.PLAYER_RADIUS ), map )
            && isFree ( ( int ) ( x - Constants.PLAYER_RADIUS ), ( int ) ( y + Constants.PLAYER_RADIUS ), map )
            && isFree ( ( int ) ( x - Constants.PLAYER_RADIUS ), ( int ) ( y - Constants.PLAYER_RADIUS ), map );
    }

    private boolean isFree ( int x, int y, DungeonMap map ) {
        int tile = map.get ( x, y );
        if(tile == Constants.EXIT && !this.hasKey) return false;
        return tile == Constants.EMPTY || tile == Constants.EXIT || tile==Constants.SAW || tile==Constants.SPIKE || tile==Constants.KEY || tile==Constants.POTION;
    }

    private void rotate ( double angle ) {
        Point2D newDirection = new Rotate ( Math.toDegrees ( angle ) ).transform ( this.directionX, this.directionY );
        this.directionX = newDirection.getX ( );
        this.directionY = newDirection.getY ( );
    }
}