package it.unical.mat.smart_playground.balltracker.tracking;

/**
 * Created by utente on 18/12/2021.
 */
public class TrackingSettings
{
    private final float minLocDelta;
    private final short minDirDelta;
    private final long  arucoDetectDelta;
    private final int   minBallDetectArea;
    private final int   colorDetectionSensitivity;
    private final boolean useColorBooster;

    public TrackingSettings( final float minLocDelta, final short minDirDelta, final long arucoDetectDelta, final int minBallDetectArea, final int colorDetectionSensitivity, final boolean useColorBooster )
    {
        this.minLocDelta = minLocDelta;
        this.minDirDelta = minDirDelta;
        this.arucoDetectDelta = arucoDetectDelta;
        this.minBallDetectArea = minBallDetectArea;
        this.colorDetectionSensitivity = colorDetectionSensitivity;
        this.useColorBooster = useColorBooster;
    }

    public float getMinBallLocationDeltaPercentage()
    {
        return minLocDelta;
    }

    public short getMinBallOrientationDelta()
    {
        return minDirDelta;
    }

    public long getArucoDetectDelta() { return arucoDetectDelta; }

    public int getMinBallDetectArea() { return minBallDetectArea; }

    public int getColorDetectionSensitivity() { return colorDetectionSensitivity; }

    public boolean getUseColorBooster() { return useColorBooster;  }
}
