package net.codepixl.GLCraft.GUI;

import com.nishu.utils.Time;
import net.codepixl.GLCraft.util.FormattedStringSet;

public class ChatMessage {
	public FormattedStringSet msg;
	public float opacity = 1f;
	public float timeAlive = 0f;
	public ChatMessage(String msg){
		this.msg = new FormattedStringSet(msg);
	}
	public void update(){
		this.timeAlive+=Time.getDelta();
		if(this.timeAlive > 10)
			this.opacity-=Time.getDelta();
		if(this.opacity <= 0)
			this.opacity = 0;
	}
}
