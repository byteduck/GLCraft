package net.codepixl.GLCraft.render;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.ArrayList;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.tile.Tile;

import com.nishu.utils.Color4f;

import org.lwjgl.util.vector.Vector3f;;

public class Shape {
	 public static void createCube(float x, float y, float z, Color4f color, float[] texCoords, float size, ArrayList<Vector3f> lights) {
		 
		 color = getColor(new Vector3f(x,y,z), lights);
		 
         if (texCoords.length == 2) {
                 // bottom face
                 glColor4f(color.r, color.g, color.b, color.a);
                 glTexCoord2f(texCoords[0], texCoords[1]);
                 glVertex3f(x, y, z + size);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
                 glVertex3f(x + size, y, z + size);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x + size, y, z);
                 glTexCoord2f(texCoords[0], texCoords[1] +Spritesheet.tiles.uniformSize());
                 glVertex3f(x, y, z);

                 // top face
                 glColor4f(color.r, color.g, color.b, color.a);
                 glTexCoord2f(texCoords[0], texCoords[1]);
                 glVertex3f(x, y + size, z);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
                 glVertex3f(x + size, y + size, z);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x + size, y + size, z + size);
                 glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x, y + size, z + size);

                 // front face
                 glColor4f(color.r, color.g, color.b, color.a);
                 glTexCoord2f(texCoords[0], texCoords[1]);
                 glVertex3f(x, y, z);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
                 glVertex3f(x + size, y, z);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x + size, y + size, z);
                 glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x, y + size, z);

                 // back face
                 glColor4f(color.r, color.g, color.b, color.a);
                 glTexCoord2f(texCoords[0], texCoords[1]);
                 glVertex3f(x, y + size, z + size);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
                 glVertex3f(x + size, y + size, z + size);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x + size, y, z + size);
                 glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x, y, z + size);

                 // left face
                 glColor4f(color.r, color.g, color.b, color.a);
                 glTexCoord2f(texCoords[0], texCoords[1]);
                 glVertex3f(x + size, y, z);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
                 glVertex3f(x + size, y, z + size);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x + size, y + size, z + size);
                 glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x + size, y + size, z);

                 // right face
                 glColor4f(color.r, color.g, color.b, color.a);
                 glTexCoord2f(texCoords[0], texCoords[1]);
                 glVertex3f(x, y, z + size);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
                 glVertex3f(x, y, z);
                 glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x, y + size, z);
                 glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.tiles.uniformSize());
                 glVertex3f(x, y + size, z + size);
         } else if (texCoords.length > 2) {
                 /*
                  * bottom - first
                  * top - second
                  * front - third
                  * back - fourth
                  * left - fifth
                  * right - sixth
                  */
                                         // bottom face (0, 1)
                                         glColor4f(color.r, color.g, color.b, color.a);
                                         glTexCoord2f(texCoords[0], texCoords[1]);
                                         glVertex3f(x, y, z + size);
                                         glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
                                         glVertex3f(x + size, y, z + size);
                                         glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1] - Spritesheet.tiles.uniformSize());
                                         glVertex3f(x + size, y, z);
                                         glTexCoord2f(texCoords[0], texCoords[1] - Spritesheet.tiles.uniformSize());
                                         glVertex3f(x, y, z);

                                         // top face (2, 3)
                                         glColor4f(color.r, color.g, color.b, color.a);
                                         glTexCoord2f(texCoords[2], texCoords[3]);
                                         glVertex3f(x, y + size, z);
                                         glTexCoord2f(texCoords[2] + Spritesheet.tiles.uniformSize(), texCoords[3]);
                                         glVertex3f(x + size, y + size, z);
                                         glTexCoord2f(texCoords[2] + Spritesheet.tiles.uniformSize(), texCoords[3] - Spritesheet.tiles.uniformSize());
                                         glVertex3f(x + size, y + size, z + size);
                                         glTexCoord2f(texCoords[2], texCoords[3] - Spritesheet.tiles.uniformSize());
                                         glVertex3f(x, y + size, z + size);

                                         // front face (4, 5)
                                         glColor4f(color.r, color.g, color.b, color.a);
                                         glTexCoord2f(texCoords[4], texCoords[5]);
                                         glVertex3f(x, y, z);
                                         glTexCoord2f(texCoords[4] + Spritesheet.tiles.uniformSize(), texCoords[5]);
                                         glVertex3f(x + size, y, z);
                                         glTexCoord2f(texCoords[4] + Spritesheet.tiles.uniformSize(), texCoords[5] - Spritesheet.tiles.uniformSize());
                                         glVertex3f(x + size, y + size, z);
                                         glTexCoord2f(texCoords[4], texCoords[5] - Spritesheet.tiles.uniformSize());
                                         glVertex3f(x, y + size, z);

                                         // back face (6, 7)
                                         glColor4f(color.r, color.g, color.b, color.a);
                                         glTexCoord2f(texCoords[6], texCoords[7]);
                                         glVertex3f(x, y + size, z + size);
                                         glTexCoord2f(texCoords[6] + Spritesheet.tiles.uniformSize(), texCoords[7]);
                                         glVertex3f(x + size, y + size, z + size);
                                         glTexCoord2f(texCoords[6] + Spritesheet.tiles.uniformSize(), texCoords[7] + Spritesheet.tiles.uniformSize());
                                         glVertex3f(x + size, y, z + size);
                                         glTexCoord2f(texCoords[6], texCoords[7] + Spritesheet.tiles.uniformSize());
                                         glVertex3f(x, y, z + size);

                                         // left face (8, 9)
                                         glColor4f(color.r, color.g, color.b, color.a);
                                         glTexCoord2f(texCoords[8], texCoords[9]);
                                         glVertex3f(x + size, y, z);
                                         glTexCoord2f(texCoords[8] + Spritesheet.tiles.uniformSize(), texCoords[9]);
                                         glVertex3f(x + size, y, z + size);
                                         glTexCoord2f(texCoords[8] + Spritesheet.tiles.uniformSize(), texCoords[9] - Spritesheet.tiles.uniformSize());
                                         glVertex3f(x + size, y + size, z + size);
                                         glTexCoord2f(texCoords[8], texCoords[9] - Spritesheet.tiles.uniformSize());
                                         glVertex3f(x + size, y + size, z);

                                         // right face (10, 11)
                                         glColor4f(color.r, color.g, color.b, color.a);
                                         glTexCoord2f(texCoords[10], texCoords[11]);
                                         glVertex3f(x, y, z + size);
                                         glTexCoord2f(texCoords[10] + Spritesheet.tiles.uniformSize(), texCoords[11]);
                                         glVertex3f(x, y, z);
                                         glTexCoord2f(texCoords[10] + Spritesheet.tiles.uniformSize(), texCoords[11] - Spritesheet.tiles.uniformSize());
                                         glVertex3f(x, y + size, z);
                                         glTexCoord2f(texCoords[10], texCoords[11] - Spritesheet.tiles.uniformSize());
                                         glVertex3f(x, y + size, z + size);
         }
	 }
	 
	 public static void createCross(float x, float y, float z, Color4f color, float[] texCoords, float size, ArrayList<Vector3f> lights){
		 
		 color = getColor(new Vector3f(x,y,z), lights);
		 
		 //face 1
		 glColor4f(color.r, color.g, color.b, color.a);
		 glTexCoord2f(texCoords[0], texCoords[1]);
         glVertex3f(x+size, y+size, z + size);
         glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.tiles.uniformSize());
         glVertex3f(x+size,y,z+size);
         glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]+Spritesheet.tiles.uniformSize());
         glVertex3f(x,y,z);
         glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
         glVertex3f(x,y+size,z);
         
         //face 1 reversed
         glColor4f(color.r, color.g, color.b, color.a);
         glTexCoord2f(texCoords[0], texCoords[1]);
         glVertex3f(x,y+size,z);
         glTexCoord2f(texCoords[0], texCoords[1]+Spritesheet.tiles.uniformSize());
         glVertex3f(x,y,z);
         glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1] + Spritesheet.tiles.uniformSize());
         glVertex3f(x+size,y,z+size);
         glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
         glVertex3f(x+size, y+size, z + size);
         
         //face 2
         glColor4f(color.r, color.g, color.b, color.a);
         glTexCoord2f(texCoords[0], texCoords[1]);
         glVertex3f(x+size, y+size, z);
         glTexCoord2f(texCoords[0], texCoords[1] + Spritesheet.tiles.uniformSize());
         glVertex3f(x+size,y,z);
         glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]+Spritesheet.tiles.uniformSize());
         glVertex3f(x,y,z+size);
         glTexCoord2f(texCoords[0] + Spritesheet.tiles.uniformSize(), texCoords[1]);
         glVertex3f(x,y+size,z+size);
         
         //face 2 reversed
         glColor4f(color.r, color.g, color.b, color.a);
         glTexCoord2f(texCoords[0], texCoords[1]);
         glVertex3f(x,y+size,z+size);
         glTexCoord2f(texCoords[0], texCoords[1]+Spritesheet.tiles.uniformSize());
         glVertex3f(x,y,z+size);
         glTexCoord2f(texCoords[0]+Spritesheet.tiles.uniformSize(), texCoords[1]+Spritesheet.tiles.uniformSize());
         glVertex3f(x+size,y,z);
         glTexCoord2f(texCoords[0]+Spritesheet.tiles.uniformSize(), texCoords[1]);
         glVertex3f(x+size, y+size, z);
	 }
	 
	 public static void createTexturelessCube(float x, float y, float z, Color4f color, float size){
         glColor4f(color.r, color.g, color.b, color.a);
		 // bottom face
         glVertex3f(x, y, z + size);
         glVertex3f(x + size, y, z + size);
         glVertex3f(x + size, y, z);
         glVertex3f(x, y, z);

         // top face
         glVertex3f(x, y + size, z);
         glVertex3f(x + size, y + size, z);
         glVertex3f(x + size, y + size, z + size);
         glVertex3f(x, y + size, z + size);

         // front face
         glVertex3f(x, y, z);
         glVertex3f(x + size, y, z);
         glVertex3f(x + size, y + size, z);
         glVertex3f(x, y + size, z);

         // back face
         glVertex3f(x, y + size, z + size);
         glVertex3f(x + size, y + size, z + size);
         glVertex3f(x + size, y, z + size);
         glVertex3f(x, y, z + size);

         // left face
         glVertex3f(x + size, y, z);
         glVertex3f(x + size, y, z + size);
         glVertex3f(x + size, y + size, z + size);
         glVertex3f(x + size, y + size, z);

         // right face
         glVertex3f(x, y, z + size);
         glVertex3f(x, y, z);
         glVertex3f(x, y + size, z);
         glVertex3f(x, y + size, z + size);
         
         glColor4f(1,1,1,1);
	 }
	 
	 private static Color4f getColor(Vector3f pos, ArrayList<Vector3f> lights){
		Constants.world.getWorldManager().s.addCurrentTile(1);
		int progress = (int) (Constants.world.getWorldManager().s.currentTilePercentage() * 0.33 + 66);
		Constants.world.getWorldManager().s.getSplash().setProgress(progress,"Lighting chunks "+progress+"%");
		 float minDist = 7;
		 Color4f color = new Color4f(0.1f,0.1f,0.1f,1.0f);
		 for(int i = 0; i < lights.size(); i++){
			 Vector3f lPos = lights.get(i);
			 float dist = MathUtils.distance(lPos, pos);
			 if(dist <= 7 && dist < minDist || dist == 0){
				 float pdist = (7-dist)/7;
				 minDist = dist;
				 color = MathUtils.mult(new Color4f(1.0f,1.0f,1.0f,1.0f), new Color4f(pdist,pdist,pdist,1.0f));
			 }
		 }
		 return color;
		 /**float light = (float)Constants.world.getWorldManager().getLight((int)pos.x, (int)pos.y, (int)pos.z, false)/(float)15;
		 return new Color4f(light,light,light,1.0f);**/
	 }
}
