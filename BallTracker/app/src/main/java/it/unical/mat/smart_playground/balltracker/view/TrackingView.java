package it.unical.mat.smart_playground.balltracker.view;

import java.util.List;

import it.unical.mat.smart_playground.balltracker.tracking.BallTracker;
import it.unical.mat.smart_playground.balltracker.tracking.Marker;
import it.unical.mat.smart_playground.balltracker.util.Vector2;
import it.unical.mat.smart_playground.balltracker.util.Vector2Int;

/**
 * Created by utente on 30/10/2020.
 */
public interface TrackingView
{
    public void drawTracking( final List<Marker> detectMarkers, final BallTracker ballTracker );
    public void drawMarker( final Marker marker );
    public void drawArrow( final Vector2Int origin, final Vector2Int arrowPoint );
    public void drawArrow( final Vector2Int origin, final Vector2Int arrowPoint, final String label );
    public void drawOrientation( final Vector2Int origin, final Vector2Int direction, final short orientation );
    public void drawCorners( final int[][] cornersCoords );
    public void drawPlatformPaddings( final Vector2<Integer> platformSize, int[] platformPaddings );
    public boolean canDraw();
}
