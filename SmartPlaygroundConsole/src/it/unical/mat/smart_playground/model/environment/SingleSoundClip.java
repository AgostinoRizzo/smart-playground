/**
 * 
 */
package it.unical.mat.smart_playground.model.environment;

import javax.sound.sampled.Clip;

/**
 * @author Agostino
 *
 */
public class SingleSoundClip implements SoundClip
{
	private Clip originClip;
	private boolean loop, muted;
	
	public SingleSoundClip( final Clip origin )
	{
		originClip = origin;
		loop = muted = false;
	}
	
	public SingleSoundClip( final Clip origin, final boolean loop )
	{
		originClip = origin;
		this.loop = loop;
	}
	
	public void setOriginClip( final Clip origin )
	{
		setOriginClip(origin, false);
	}
	
	public void setOriginClip( final Clip origin, final boolean loop )
	{
		originClip = origin;
		this.loop = loop;
	}
	
	@Override
	public void play()
	{
		if ( originClip != null && !muted )
		{
			originClip.stop();
			originClip.setFramePosition(0);
			if ( loop )
				originClip.loop(Clip.LOOP_CONTINUOUSLY);
			else
				originClip.start();
		}
	}

	@Override
	public void stop()
	{
		if ( originClip != null )
		{
			originClip.stop();
			originClip.setFramePosition(0);
		}
	}
	
	@Override
	public void mute()
	{
		muted = true;
	}
}
