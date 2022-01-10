/**
 * 
 */
package it.unical.mat.smart_playground.model.environment;

/**
 * @author Agostino
 *
 */
public class DelayedSoundTrigger extends Thread
{
	private final EnvironmentSoundType soundType;
	private final long delay;
	
	public DelayedSoundTrigger( final EnvironmentSoundType soundType, final long delay )
	{
		setDaemon(true);
		this.soundType = soundType;
		this.delay = delay;
	}
	
	@Override
	public void run()
	{
		try { sleep(delay); } 
		catch (InterruptedException e) {}
		
		EnvironmentSoundPlayer.getInstance().playSound(soundType);
	}
}
