package it.unical.mat.smart_playground.balltracker.tracking;

import it.unical.mat.smart_playground.balltracker.util.Vector2;

/**
 * Created by utente on 28/10/2020.
 */
public class BallStatus
{
    private final Vector2<Float> location = new Vector2<>(0.0f, 0.0f);
    private short orientation = 0;  // 0-359 degrees

    public Vector2<Float> getLocation()
    {
        return location;
    }

    public short getOrientation()
    {
        return orientation;
    }

    public void setOrientation( final short orientation )
    {
        this.orientation = orientation;
    }
}
