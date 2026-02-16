package it.unical.mat.smart_playground.balltracker.tracking;

/**
 * Created by utente on 01/11/2020.
 */
public class DelayedStatusFlag
{
    private final long getTime;
    private Long firstSetTime = null;

    public DelayedStatusFlag( final long getTime )
    {
        this.getTime = getTime;
    }

    public void onSet()
    {
        if ( firstSetTime == null )
            firstSetTime = System.currentTimeMillis();
    }

    public void onClear()
    {
        firstSetTime = null;
    }

    public boolean tryGet()
    {
        return ( firstSetTime != null && (System.currentTimeMillis() - firstSetTime) >= getTime );
    }
}
