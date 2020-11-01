package it.unical.mat.smart_playground.balltracker.tracking;

/**
 * Created by utente on 31/10/2020.
 */
public class TrackingCommStats
{
    private static final long KEEP_ALIVE_TIME = 2000;  // expressed in milliseconds.

    private static TrackingCommStats instance = null;

    private final KeepAliveCommStat ballLocationCommStat    = new KeepAliveCommStat(KEEP_ALIVE_TIME);
    private final KeepAliveCommStat ballOrientationCommStat = new KeepAliveCommStat(KEEP_ALIVE_TIME);

    public static TrackingCommStats getInstance()
    {
        if ( instance == null )
            instance = new TrackingCommStats();
        return instance;
    }

    private TrackingCommStats()
    {}

    public KeepAliveCommStat getBallLocationCommStat()
    {
        return ballLocationCommStat;
    }

    public KeepAliveCommStat getBallOrientationCommStat()
    {
        return ballOrientationCommStat;
    }
}
