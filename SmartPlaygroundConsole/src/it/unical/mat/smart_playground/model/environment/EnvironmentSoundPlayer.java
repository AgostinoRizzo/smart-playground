/**
 * 
 */
package it.unical.mat.smart_playground.model.environment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author Agostino
 *
 */
public class EnvironmentSoundPlayer
{
	private static final String RACKET_SWING_SOUND_FILENAME     = "./res/sounds/racket_swing.wav";
	private static final String RACKET_HIT_SOUND_FILENAME       = "./res/sounds/racket_hit.wav";
	private static final String CLUB_ATTEMPT_SOUND_FILENAME     = "./res/sounds/club_attempt.wav";
	private static final String CLUB_SWING_SOUND_FILENAME       = "./res/sounds/club_swing.wav";
	private static final String CLUB_SWING_LIGHT_SOUND_FILENAME = "./res/sounds/club_swing_light.wav";
	private static final String BALL_BOUNCE_SOUND_FILENAME      = "./res/sounds/ball_bounce.wav";
	private static final String BALL_IN_HOLE_SOUND_FILENAME     = "./res/sounds/ball_in_hole.wav";
	private static final String TENNIS_FINISH_SOUND_FILENAME    = "./res/sounds/tennis_finish.wav";
	private static final String GAME_READY_SOUND_FILENAME       = "./res/sounds/game_ready.wav";
	private static final String GAME_WIN_SOUND_FILENAME         = "./res/sounds/game_win.wav";
	private static final String GAME_LOSE_SOUND_FILENAME        = "./res/sounds/game_lose.wav";
	
	private static final String CONFIRM_SOUND_FILENAME          = "./res/sounds/confirm.wav";
	private static final String CHOOSE_SOUND_FILENAME           = "./res/sounds/choose.wav";
	private static final String SUB_CHOOSE_SOUND_FILENAME       = "./res/sounds/sub_choose.wav";
	private static final String CLAP_SOUND_FILENAME             = "./res/sounds/clap.wav";
	private static final String WIND_SOUND_FILENAME             = "./res/sounds/wind.wav";
	private static final String STEP_SOUND_FILENAME             = "./res/sounds/step_?.wav";
	
	private static EnvironmentSoundPlayer instance = null;
	
	private final Map<EnvironmentSoundType, SoundClip> soundClipsMap = new HashMap<>();
	private final Lock lock = new ReentrantLock();
	
	public static EnvironmentSoundPlayer getInstance()
	{
		if ( instance == null )
			instance = new EnvironmentSoundPlayer();
		return instance;
	}
	
	private EnvironmentSoundPlayer()
	{
		try
		{
			lock.lock();
			
			createSoundClip(EnvironmentSoundType.RACKET_SWING,     RACKET_SWING_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.RACKET_HIT,       RACKET_HIT_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.CLUB_ATTEMPT,     CLUB_ATTEMPT_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.CLUB_SWING,       CLUB_SWING_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.CLUB_SWING_LIGHT, CLUB_SWING_LIGHT_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.BALL_BOUNCE,      BALL_BOUNCE_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.BALL_IN_HOLE,     BALL_IN_HOLE_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.TENNIS_FINISH,    TENNIS_FINISH_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.GAME_READY,       GAME_READY_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.GAME_WIN,         GAME_WIN_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.GAME_LOSE,        GAME_LOSE_SOUND_FILENAME);
			
			createSoundClip(EnvironmentSoundType.CONFIRM,     CONFIRM_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.CHOOSE,      CHOOSE_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.SUB_CHOOSE,  SUB_CHOOSE_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.CLAP,        CLAP_SOUND_FILENAME);
			createSoundClip(EnvironmentSoundType.WIND,        WIND_SOUND_FILENAME, true);
			
			createCirclularSoundClip(EnvironmentSoundType.STEP, STEP_SOUND_FILENAME, 4);
			
			lock.unlock();
		} 
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
		{}
	}
	
	public void playSound( final EnvironmentSoundType soundType )
	{
		lock.lock();
		if ( soundClipsMap.containsKey(soundType) )
			soundClipsMap.get(soundType).play();
		lock.unlock();
	}
	
	public void stopSound( final EnvironmentSoundType soundType )
	{
		lock.lock();
		if ( soundClipsMap.containsKey(soundType) )
			soundClipsMap.get(soundType).stop();
		lock.unlock();
	}
	
	public void onTennisGameScoreUpdate()
	{
		playSound(EnvironmentSoundType.GAME_WIN);
		playSound(EnvironmentSoundType.CLAP);
	}
	
	public void onGolfGameHole()
	{
		playSound(EnvironmentSoundType.BALL_IN_HOLE);
		new DelayedSoundTrigger(EnvironmentSoundType.GAME_WIN, 1500).start();
		new DelayedSoundTrigger(EnvironmentSoundType.CLAP,     1500).start();
	}
	
	public void onWindStatusChanged( final boolean windOn )
	{
		if ( windOn ) playSound(EnvironmentSoundType.WIND);
		else stopSound(EnvironmentSoundType.WIND);
	}
	
	private void createSoundClip( final EnvironmentSoundType soundType, final String soundFilename )
			throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		createSoundClip(soundType, soundFilename, false);
	}
	
	private void createSoundClip( final EnvironmentSoundType soundType, final String soundFilename, final boolean loop )
			throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		soundClipsMap.put(soundType, new SingleSoundClip(createClip(soundFilename), loop));
	}
	
	private void createCirclularSoundClip( final EnvironmentSoundType soundType, final String soundFilename, final int soundsCount )
			throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		final int idIndex = soundFilename.indexOf('?');
		if ( idIndex < 0 )
			return;
		
		final int idIndexPlusOne = idIndex + 1;
		final StringBuilder soundFilenameBuilder = new StringBuilder(soundFilename);
		final CircularSoundClip soundClip = new CircularSoundClip();
		
		for ( int id=1; id<=soundsCount; ++id )
		{
			soundFilenameBuilder.replace(idIndex, idIndexPlusOne, Integer.toString(id));
			soundClip.appendClip(createClip(soundFilenameBuilder.toString()));
		}
		soundClipsMap.put(soundType, soundClip);
	}
	
	private static Clip createClip( final String soundFilename ) 
			throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFilename).getAbsoluteFile());
		final Clip clip = AudioSystem.getClip();
		clip.open(audioInputStream);
		return clip;
	}
}
