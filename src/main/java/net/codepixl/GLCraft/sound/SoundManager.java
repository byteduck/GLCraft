package net.codepixl.GLCraft.sound;

import org.lwjgl.util.vector.Vector3f;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {
	private static SoundManager mainManager;
	private SoundSystem system;
	private Vector3f pos = new Vector3f();
	
	public SoundManager(){
		try{
			SoundSystemConfig.addLibrary( LibraryLWJGLOpenAL.class );
		}catch( SoundSystemException e ){
			System.err.println("Error initializing soundsystem.");
		}
		
		try{
			SoundSystemConfig.setCodec( "wav", CodecWav.class );
		}catch(SoundSystemException e){
			System.err.println("Error initializing wav codec.");
		}
		
		try{
			system = new SoundSystem(LibraryLWJGLOpenAL.class);
		}catch(SoundSystemException e){
			system = new SoundSystem();
			System.err.println("Error initializing sound system.");
		}
	}
	
	public void quickPlay(String soundName){
		system.setListenerPosition(0, 0, 0);
		system.quickPlay(true,soundName+".wav",false,0f,0f,0f,0,0f);
		system.setListenerPosition(pos.x, pos.y, pos.z);
	}
	
	public void quickPlayOnce(String soundName){
		if(!system.playing(soundName)){
			quickPlay(soundName);
		}
	}
	
	public void quickPlay(String soundName, Vector3f pos) {
		system.quickPlay(true,soundName+".wav",false,pos.x,pos.y,pos.z,SoundSystemConfig.ATTENUATION_LINEAR,30f);
	}
	
	public void setPosAndRot(Vector3f pos, Vector3f rot){
		this.pos = pos;
		system.setListenerPosition(pos.x, pos.y, pos.z);
		//TODO: Make rotation work
		//system.setListenerOrientation(rot.x/360f, rot.y/360f, rot.z/360f, 0, 0, 0);
	}
	
	public void play(String name){
		if(!system.playing(name+".wav")){
			system.loadSound(name+".wav");
			system.play(name+".wav");
		}
	}

	public static void setMainManager(SoundManager soundManager) {
		SoundManager.mainManager = soundManager;
	} 
	
	public static SoundManager getMainManager(){
		return mainManager;
	}
}
