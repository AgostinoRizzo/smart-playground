package it.unical.mat.smart_playground.balltracker.tracking;

/**
 * Created by utente on 18/12/2021.
 */
public class TrackingSettings
{
    private final float minLocDelta;
    private final short minDirDelta;

    public TrackingSettings( final float minLocDelta, final short minDirDelta )
    {
        this.minLocDelta = minLocDelta;
        this.minDirDelta = minDirDelta;
    }

    public float getMinBallLocationDeltaPercentage()
    {
        return minLocDelta;
    }

    public short getMinBallOrientationDelta()
    {
        return minDirDelta;
    }
}
