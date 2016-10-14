package net.codepixl.GLCraft.GUI;

import com.nishu.utils.Time;

public class ChatMessage {
	public String msg;
	public float opacity = 1f;
	public float timeAlive = 0f;
	public ChatMessage(String msg){
		this.msg = msg;
	}
	public void update(){
		this.timeAlive+=Time.getDelta();
		if(this.timeAlive > 10)
			this.opacity-=Time.getDelta();
		if(this.opacity <= 0)
			this.opacity = 0;
	}
}
