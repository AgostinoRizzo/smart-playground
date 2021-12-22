package it.unical.mat.smart_playground.motioncontroller;

/**
 * Created by utente on 20/12/2021.
 */
public interface MotionCommunicator
{
    // each method returns the sending time (<0 value for no send)
    public long sendOrientation( final float orientation );
    public long sendOrientationSync( final float orientation );
    public long sendUnknownOrientation();
    public long sendSteps( final int totalSteps );
}
