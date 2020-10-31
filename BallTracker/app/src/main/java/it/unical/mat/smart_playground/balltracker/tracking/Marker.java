package it.unical.mat.smart_playground.balltracker.tracking;

import it.unical.mat.smart_playground.balltracker.util.Vector2Int;
import it.unical.mat.smart_playground.balltracker.view.OpenCVTrackingView;

/**
 * Created by utente on 05/10/2020.
 */
public class Marker
{
    private static final short X = 0, Y = 1;

    private final int[][] cornersCoords;
    private final int id;

    public Marker( final Integer[][] cornersCoords, final int id )
    {
        this.cornersCoords = Marker.<Integer>cloneCornersCoordsArray(cornersCoords);
        this.id = id;
    }

    public Marker( final Double[][] cornersCoords, final int id )
    {
        this.cornersCoords = Marker.<Double>cloneCornersCoordsArray(cornersCoords);
        this.id = id;
    }

    public int[][] getCornersCoords()
    {
        return cornersCoords;
    }

    public int getId()
    {
        return id;
    }

    public Vector2Int getCenter()
    {
        final Vector2Int center = new Vector2Int(0, 0);
        for ( int i=0; i< cornersCoords.length; ++i )
        {
            center.setX( center.getX() + cornersCoords[i][X] );
            center.setY( center.getY() + cornersCoords[i][Y] );
        }
        center.setX( center.getX() / 4 );
        center.setY( center.getY() / 4 );
        return center;
    }

    public Vector2Int getDirection()
    {
        final Vector2Int v = Vector2Int.fromPoints( new Vector2Int(cornersCoords[3][0], cornersCoords[3][1]),   // bottom-left, top-left
                new Vector2Int(cornersCoords[0][0], cornersCoords[0][1]));
        final Vector2Int w = Vector2Int.fromPoints( new Vector2Int(cornersCoords[2][0], cornersCoords[2][1]),   // bottom-right, top-right
                new Vector2Int(cornersCoords[1][0], cornersCoords[1][1]));
        return Vector2Int.avg(v, w);
    }

    public short getOrientation() throws NoOrientationDetectedException
    {
        final Vector2Int d = getDirection();

        if ( d.isZero() )
            throw new NoOrientationDetectedException();
        else
        {
            final int dx = d.getX();
            final int dy = d.getY();

            final int Yx = Vector2Int.Y.getX();
            final int Yy = Vector2Int.Y.getY();

            /*** full formula where Y = (Yx=0, Yy=1) ***/
            //final double relativeAngle = Math.acos( ((dx * Yx) + (dy * Yy)) /
            //        ( Math.sqrt((dx * dx) + (dy * dy)) * Math.sqrt((Yx * Yx) + (Yy * Yy)) ) );

            // simplified formula
            final double relativeAngle = Math.acos( (dy * Yy) /
                    ( Math.sqrt((dx * dx) + (dy * dy)) * Math.sqrt(Yy * Yy) ) );

            final double absoluteAngle = (dx >= 0) ? relativeAngle : (Math.PI * 2.0) - relativeAngle;
            double degrees = Math.toDegrees(absoluteAngle);

            if ( degrees < 0.0 || degrees >= 360.0 )
                degrees = 0.0;

            int roundedDegrees = (int)Math.round(degrees);
            if ( roundedDegrees > 0 )
                roundedDegrees = 360 - roundedDegrees;
            roundedDegrees = (roundedDegrees + 180) % 360;

            return (short)roundedDegrees;
        }
    }

    private static <T> int[][] cloneCornersCoordsArray( final T[][] cornersCoords )
    {
        if ( cornersCoords.length <= 0 || cornersCoords[0].length != 2 )
            throw new RuntimeException("Invalid corners coordinates.");

        final int[][] clone = new int[cornersCoords.length][2];
        int j;
        for ( int i=0; i<cornersCoords.length; ++i )
            for ( j=0; j<2; ++j )
                clone[i][j] = ((Number) cornersCoords[i][j]).intValue();
        return clone;
    }
}
