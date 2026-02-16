package it.unical.mat.smart_playground.balltracker.tracking;

import it.unical.mat.smart_playground.balltracker.util.Vector2Int;

/**
 * Created by utente on 09/02/2022.
 */
public class StaticallyOrientedMarker extends Marker
{
    private Vector2Int direction = new Vector2Int(Vector2Int.X.not());

    public StaticallyOrientedMarker( final Integer[][] cornersCoords, final int id )
    { super(cornersCoords, id); }

    public StaticallyOrientedMarker( final Double[][] cornersCoords, final int id )
    { super(cornersCoords, id); }

    public Vector2Int getDirection() {  return direction; }
    public void setDirection(Vector2Int direction) { this.direction = direction;  }
}
