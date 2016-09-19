package net.codepixl.GLCraft.world.entity.mob;

public class DeathMessage {
	public static String getMessage(String name, DamageSource source){
		switch(source){
			case MOB:
				return name+" was attacked and died.";
			case FALL:
				return name+" fell to an untimely death.";
			case DROWNING:
				return name+" thought they could breathe underwater.";
			case ENVIRONMENT:
				return name+" died.";
			case VOID:
				return name+" fell into the void.";
			case FIRE:
				return name+" burned to a crisp.";
		}
		return name+" died, but the universe is also out of wack.";
	}
}
