package it.unical.mat.smart_playground.balltracker.tracking;

import it.unical.mat.smart_playground.balltracker.util.Vector2;

/**
 * Created by utente on 05/10/2020.
 */
public interface BallTrackingCommunicator
{
    public void sendBallTrackingLocation( final BallStatus status );
    public void sendBallTrackingOrientation( final BallStatus status );
    public void sendBallTrackingStatus( final BallStatus status );
    public void sendUnknownBallTrackingStatus();
    public void sendGolfHoleTrackingLocation( final Vector2<Float> newGolfHoleLocation );
}
