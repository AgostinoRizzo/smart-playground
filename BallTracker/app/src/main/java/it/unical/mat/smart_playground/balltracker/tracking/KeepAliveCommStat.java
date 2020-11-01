package it.unical.mat.smart_playground.balltracker.tracking;

/**
 * Created by utente on 31/10/2020.
 */
public class KeepAliveCommStat
{
    private final long keepAliveTime;
    private long lastCommTime = Long.MIN_VALUE;

    public KeepAliveCommStat( final long keepAliveTime )
    {
        this.keepAliveTime = keepAliveTime;
    }

    public void onComm()
    {
        lastCommTime = System.currentTimeMillis();
    }

    public boolean onKeepAlive()
    {
        return (System.currentTimeMillis() - lastCommTime) >= keepAliveTime;
    }
}
