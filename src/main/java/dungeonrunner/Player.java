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

    public Player ( double startX, double startY ) {
        this.positionX = startX;
        this.positionY = startY;
        this.directionX =  1.0;
        this.directionY =  0.0;
    }

    public double getPositionX ( ) { return this.positionX; }
    public double getPositionY ( ) { return this.positionY; }
    public double getDirectionX ( ) { return this.directionX; }
    public double getDirectionY ( ) { return this.directionY; }

    public void setMoveForward  ( boolean newValue ) { this.moveForward  = newValue; }
    public void setMoveBackward ( boolean newValue ) { this.moveBackward = newValue; }
    public void setRotateLeft   ( boolean newValue ) { this.rotateLeft   = newValue; }
    public void setRotateRight  ( boolean newValue ) { this.rotateRight  = newValue; }

    public void update ( DungeonMap map ) {
        if ( this.moveForward ) {
            double newX = this.positionX + this.directionX * Constants.PLAYER_MOVE_SPEED;
            double newY = this.positionY + this.directionY * Constants.PLAYER_MOVE_SPEED;

            if ( canMoveTo ( newX, positionY, map ) ) {
                this.positionX = newX;
            }

            if ( canMoveTo ( positionX, newY, map ) ) {
                this.positionY = newY;
            }
        }

        if ( this.moveBackward ) {
            double newX = this.positionX - this.directionX * Constants.PLAYER_MOVE_SPEED;
            double newY = this.positionY - this.directionY * Constants.PLAYER_MOVE_SPEED;

            if ( canMoveTo ( newX, positionY, map ) ) {
                this.positionX = newX;
            }

            if ( canMoveTo ( positionX, newY, map ) ) {
                this.positionY = newY;
            }
        }

        if ( this.rotateLeft ) {
            rotate ( Constants.PLAYER_ROTATION_SPEED );
        }
        if ( this.rotateRight ) {
            rotate ( -Constants.PLAYER_ROTATION_SPEED );
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
        return tile == Constants.EMPTY || tile == Constants.EXIT || tile==Constants.SAW || tile==Constants.SPIKE;
    }

    private void rotate ( double angle ) {
        Point2D newDirection = new Rotate ( Math.toDegrees ( angle ) ).transform ( this.directionX, this.directionY );
        this.directionX = newDirection.getX ( );
        this.directionY = newDirection.getY ( );
    }
}