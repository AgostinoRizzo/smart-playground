package it.unical.mat.smart_playground.balltracker.tracking;

import org.opencv.core.MatOfPoint2f;

import java.util.Arrays;

import it.unical.mat.smart_playground.balltracker.util.Vector2;

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

    public Vector2<Integer> getCenter()
    {
        final Vector2<Integer> center = new Vector2<>(0, 0);
        for ( int i=0; i< cornersCoords.length; ++i )
        {
            center.setX( center.getX() + cornersCoords[i][X] );
            center.setY( center.getY() + cornersCoords[i][Y] );
        }
        center.setX( center.getX() / 4 );
        center.setY( center.getY() / 4 );
        return center;
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
