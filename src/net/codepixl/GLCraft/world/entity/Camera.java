package net.codepixl.GLCraft.world.entity;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_TRANSFORM_BIT;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.io.IOException;

import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Time;

public class Camera extends Entity {
	 
    private float speed, maxU, maxD;

    /*
     * Rotation x - pitch y - yaw z - roll
     */

    public Camera(float x, float y, float z, float speed, float maxU, float maxD, int id, WorldManager worldManager) {
            super(x, y, z, 0, 0, 0, id, worldManager);
            this.speed = speed;
            this.maxU = maxU;
            this.maxD = maxD;
    }

    public Camera(float x, float y, float z, float rx, float ry, float rz, float speed, float maxU, float maxD, int id, WorldManager worldManager) {
            super(x, y, z, rx, ry, rz, id, worldManager);
            this.speed = speed;
            this.maxU = maxU;
            this.maxD = maxD;
    }

    public void updateMouse() {
            float dx = Mouse.getDX() * speed * 0.16f;
            float dy = Mouse.getDY() * speed * 0.16f;

            if (getYaw() + dx >= 360) {
                    setYaw(getYaw() + dx - 360);
            } else if (getYaw() + dx < 0) {
                    setYaw(360 - getYaw() + dx);
            } else {
                    setYaw(getYaw() + dx);
            }

            if (getPitch() - dy >= maxD && getPitch() - dy <= maxU) {
                    setPitch(getPitch() + -dy);
            } else if (getPitch() - dy < maxD) {
                    setPitch(maxD);
            } else if (getPitch() - dy > maxU) {
                    setPitch(maxU);
            }
            if((dx != 0 || dy != 0) && Constants.isMultiplayer){
    			try {
					Constants.packetsToSend.write(new String("GLCRAFT_ROT_PLAYER||"+getYaw()+","+getPitch()+","+getRoll()+";").getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
    }

    public void updateKeyboard(float delay, float speed) {
            boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_W);
            boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_S);
            boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
            boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
            boolean space = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
            boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);

            if (keyUp && keyRight && !keyLeft && !keyDown) {
                    move(speed * delay * (float) Time.getDelta(), 0, -speed * delay * (float) Time.getDelta());
            }
            if (keyUp && keyLeft && !keyRight && !keyDown) {
                    move(-speed * delay * (float) Time.getDelta(), 0, -speed * delay * (float) Time.getDelta());
            }
            if (keyUp && !keyLeft && !keyRight && !keyDown) {
                    move(0, 0, -speed * delay * (float) Time.getDelta());
            }
            if (keyDown && keyLeft && !keyRight && !keyUp) {
                    move(-speed * delay * (float) Time.getDelta(), 0, speed * delay * (float) Time.getDelta());
            }
            if (keyDown && keyRight && !keyLeft && !keyUp) {
                    move(speed * delay * (float) Time.getDelta(), 0, speed * delay * (float) Time.getDelta());
            }
            if (keyDown && !keyUp && !keyLeft && !keyRight) {
                    move(0, 0, speed * delay * (float) Time.getDelta());
            }
            if (keyLeft && !keyRight && !keyUp && !keyDown) {
                    move(-speed * delay * (float) Time.getDelta(), 0, 0);
            }
            if (keyRight && !keyLeft && !keyUp && !keyDown) {
                    move(speed * delay * (float) Time.getDelta(), 0, 0);
            }
            if (space && !shift) {
                    goVert(getY() + speed * delay * (float) Time.getDelta());
            }
            if (shift && !space) {
                    goVert(getY() - speed * delay * (float) Time.getDelta());
            }
    }

    public void move(float x, float y, float z) {
    	float toZ = (float) (getZ() + (x * (float) Math.cos(Math.toRadians(getYaw() - 90)) + z * Math.cos(Math.toRadians(getYaw()))));
    	float toX = (float) (getX() - (x * (float) Math.sin(Math.toRadians(getYaw() - 90)) + z * Math.sin(Math.toRadians(getYaw()))));
    	float toY = (float) (getY() + (y * (float) Math.sin(Math.toRadians(getPitch() - 90)) + z * Math.sin(Math.toRadians(getPitch()))));
    	boolean canMove = false;
    	if(worldManager.getTileAtPos((int)toX,(int)toY,(int)toZ) != 0 && worldManager.getTileAtPos((int)toX,(int)toY,(int)toZ) != -1){
    		//System.out.println("AABB");
    		AABB playerAABB = new AABB(1.5f,1.7f,1.5f);
        	playerAABB.update(new Vector3f(toX,toY,toZ));
        	AABB cubeAABB1 = new AABB(1,1,1);
    		cubeAABB1.update(new Vector3f((int)toX+0.5f,(int)toY+0.5f,(int)toZ+0.5f));
        	canMove = AABB.testAABB(playerAABB, cubeAABB1);
    	}else if(worldManager.getTileAtPos((int)toX,(int)toY-2,(int)toZ) != 0 && worldManager.getTileAtPos((int)toX,(int)toY-1,(int)toZ) != -1){
    		AABB playerAABB = new AABB(1.5f,1.7f,1.5f);
        	playerAABB.update(new Vector3f(toX,toY,toZ));
    		AABB cubeAABB2 = new AABB(1,1,1);
    		cubeAABB2.update(new Vector3f((int)toX+0.5f,(int)toY-0.5f,(int)toZ+0.5f));
    		canMove = AABB.testAABB(playerAABB, cubeAABB2);
    	}
    	//boolean canMove = worldManager.getTileAtPos((int)toX,(int)toY,(int)toZ) == 0 || worldManager.getTileAtPos((int)toX,(int)toY,(int)toZ) == -1;
    	if(!canMove){
    		setZ(toZ);
    		setX(toX);
    		setY(toY);
    		try {
    			if(Constants.isMultiplayer){
    				Constants.packetsToSend.write(new String("GLCRAFT_MOVE_PLAYER||"+toX+","+toY+","+toZ+";").getBytes());
    			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    public void goVert(float to){
    	boolean canMove = false;
    	if(worldManager.getTileAtPos((int)this.getX(),(int)to,(int)this.getZ()) != 0 && worldManager.getTileAtPos((int)this.getX(),(int)to,(int)this.getZ()) != -1){
    		AABB playerAABB = new AABB(1.5f,2.5f,1.5f);
        	playerAABB.update(new Vector3f(this.getX(),to,this.getZ()));
        	AABB cubeAABB1 = new AABB(1,1,1);
    		cubeAABB1.update(new Vector3f((int)this.getX()+0.5f,(int)to+0.5f,(int)this.getZ()+0.5f));
        	canMove = AABB.testAABB(playerAABB, cubeAABB1);
    	}else if(worldManager.getTileAtPos((int)this.getX(),(int)to-2,(int)this.getZ()) != 0 && worldManager.getTileAtPos((int)this.getX(),(int)to-1,(int)this.getZ()) != -1){
    		AABB playerAABB = new AABB(1.5f,2.5f,1.5f);
        	playerAABB.update(new Vector3f(this.getX(),to,this.getZ()));
    		AABB cubeAABB2 = new AABB(1,1,1);
    		cubeAABB2.update(new Vector3f((int)this.getX()+0.5f,(int)to-0.5f,(int)this.getZ()+0.5f));
    		canMove = AABB.testAABB(playerAABB, cubeAABB2);
    	}
    	
    	if(!canMove){
    		setY(to);
    		try {
    			if(Constants.isMultiplayer){
    				Constants.packetsToSend.write(new String("GLCRAFT_MOVE_PLAYER||"+getX()+","+to+","+getZ()+";").getBytes());
    			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
   
    public void applyTranslations() {
            glPushAttrib(GL_TRANSFORM_BIT);
            glMatrixMode(GL_MODELVIEW);
            glRotatef(getPitch(), 1, 0, 0);
            glRotatef(getYaw(), 0, 1, 0);
            glRotatef(getRoll(), 0, 0, 1);
            glTranslatef(-getX(), -getY(), -getZ());
            glPopAttrib();
    }
}