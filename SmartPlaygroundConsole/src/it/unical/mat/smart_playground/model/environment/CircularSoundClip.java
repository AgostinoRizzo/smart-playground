/**
 * 
 */
package it.unical.mat.smart_playground.model.environment;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Clip;

/**
 * @author Agostino
 *
 */
public class CircularSoundClip implements SoundClip
{
	private final List<Clip> clips = new ArrayList<>();
	private final SingleSoundClip currentSoundClip = new SingleSoundClip(null);
	private int currentClipIndex = 0;
	
	public void appendClip( final Clip c )
	{
		clips.add(c);
	}

	@Override
	public void play()
	{
		currentSoundClip.setOriginClip(nextClip());
		currentSoundClip.play();
	}

	@Override
	public void stop()
	{
		currentSoundClip.stop();
	}
	
	private Clip nextClip()
	{
		if ( clips.isEmpty() )
			return null;
		
		final Clip c = clips.get(currentClipIndex);
		
		++currentClipIndex;
		if ( currentClipIndex >= clips.size() )
			currentClipIndex = 0;
		
		return c;
	}
}
