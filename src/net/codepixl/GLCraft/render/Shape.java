package net.codepixl.GLCraft.render;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.lwjgl.util.vector.Vector3f;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.util.Spritesheet;;

public class Shape {
	
	public static Spritesheet currentSpritesheet = Spritesheet.atlas;
	
	public static void createCube(float x, float y, float z, Color4f color, float[] texCoords, float size) {
		createRect(x,y,z,color,texCoords,size,size,size);
	}
	
	public static void createCube(float x, float y, float z, Color4f[] color, float[] texCoords, float size) {
		createRect(x,y,z,color,texCoords,size,size,size);
	}
	
	public static void createRect(float x, float y, float z, Color4f color, float[] texCoords, float sizeX, float sizeY, float sizeZ){
		try{
			//color = getColor(new Vector3f(x, y, z));

			if (texCoords.length == 2) {
				// bottom face
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[0], texCoords[1]);
				glVertex3f(x, y, z + sizeZ);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
				glVertex3f(x + sizeX, y, z + sizeZ);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y, z);
				glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x, y, z);

				// top face
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[0], texCoords[1]);
				glVertex3f(x, y + sizeY, z);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
				glVertex3f(x + sizeX, y + sizeY, z);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
				glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x, y + sizeY, z + sizeZ);

				// front face
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x, y, z);
				glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y, z);
				glTexCoord2f(texCoords[0], texCoords[1]);
				glVertex3f(x + sizeX, y + sizeY, z);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
				glVertex3f(x, y + sizeY, z);

				// back face
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[0], texCoords[1]);
				glVertex3f(x, y + sizeY, z + sizeZ);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
				glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y, z + sizeZ);
				glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x, y, z + sizeZ);

				// left face
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y, z);
				glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y, z + sizeZ);
				glTexCoord2f(texCoords[0], texCoords[1]);
				glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
				glVertex3f(x + sizeX, y + sizeY, z);

				// right face
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[0], texCoords[1]);
				glVertex3f(x, y, z + sizeZ);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
				glVertex3f(x, y, z);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x, y + sizeY, z);
				glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x, y + sizeY, z + sizeZ);
			} else if (texCoords.length > 2) {
				/*
				 * bottom - first top - second front - third back - fourth left -
				 * fifth right - sixth
				 */
				// bottom face (0, 1)
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[0], texCoords[1]);
				glVertex3f(x, y, z + sizeZ);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
				glVertex3f(x + sizeX, y, z + sizeZ);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y, z);
				glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x, y, z);

				// top face (2, 3)
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[2], texCoords[3]);
				glVertex3f(x, y + sizeY, z);
				glTexCoord2f(texCoords[2] + currentSpritesheet.uniformSize(), texCoords[3]);
				glVertex3f(x + sizeX, y + sizeY, z);
				glTexCoord2f(texCoords[2] + currentSpritesheet.uniformSize(), texCoords[3] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
				glTexCoord2f(texCoords[2], texCoords[3] + currentSpritesheet.uniformSize());
				glVertex3f(x, y + sizeY, z + sizeZ);

				// front face (4, 5)
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[4], texCoords[5] + currentSpritesheet.uniformSize());
				glVertex3f(x, y, z);
				glTexCoord2f(texCoords[4] + currentSpritesheet.uniformSize(), texCoords[5] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y, z);
				glTexCoord2f(texCoords[4] + currentSpritesheet.uniformSize(), texCoords[5]);
				glVertex3f(x + sizeX, y + sizeY, z);
				glTexCoord2f(texCoords[4], texCoords[5]);
				glVertex3f(x, y + sizeY, z);

				// back face (6, 7)
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[6], texCoords[7]);
				glVertex3f(x, y + sizeY, z + sizeZ);
				glTexCoord2f(texCoords[6] + currentSpritesheet.uniformSize(), texCoords[7]);
				glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
				glTexCoord2f(texCoords[6] + currentSpritesheet.uniformSize(), texCoords[7] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y, z + sizeZ);
				glTexCoord2f(texCoords[6], texCoords[7] + currentSpritesheet.uniformSize());
				glVertex3f(x, y, z + sizeZ);

				// left face (8, 9)
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[8], texCoords[9] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y, z);
				glTexCoord2f(texCoords[8] + currentSpritesheet.uniformSize(), texCoords[9] + currentSpritesheet.uniformSize());
				glVertex3f(x + sizeX, y, z + sizeZ);
				glTexCoord2f(texCoords[8] + currentSpritesheet.uniformSize(), texCoords[9]);
				glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
				glTexCoord2f(texCoords[8], texCoords[9]);
				glVertex3f(x + sizeX, y + sizeY, z);

				// right face (10, 11)
				glColor4f(color.r, color.g, color.b, color.a);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x, y, z + sizeZ);
				glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
				glVertex3f(x, y, z);
				glTexCoord2f(texCoords[0], texCoords[1]);
				glVertex3f(x, y + sizeY, z);
				glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
				glVertex3f(x, y + sizeY, z + sizeZ);
			}
			}catch(NullPointerException e){
				System.out.println("Error rendering rect: Texcoords null");
				e.printStackTrace();
				System.exit(0);
			}
	}
	
	public static void createRect(float x, float y, float z, Color4f[] color, float[] texCoords, float sizeX, float sizeY, float sizeZ) {
		try{
		//color = getColor(new Vector3f(x, y, z));

		if (texCoords.length == 2) {
			// bottom face
			glColor4f(color[0].r, color[0].g, color[0].b, color[0].a);
			glTexCoord2f(texCoords[0], texCoords[1]);
			glVertex3f(x, y, z + sizeZ);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
			glVertex3f(x + sizeX, y, z + sizeZ);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y, z);
			glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x, y, z);

			// top face
			glColor4f(color[1].r, color[1].g, color[1].b, color[1].a);
			glTexCoord2f(texCoords[0], texCoords[1]);
			glVertex3f(x, y + sizeY, z);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
			glVertex3f(x + sizeX, y + sizeY, z);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
			glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x, y + sizeY, z + sizeZ);

			// front face
			glColor4f(color[2].r, color[2].g, color[2].b, color[2].a);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x, y, z);
			glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y, z);
			glTexCoord2f(texCoords[0], texCoords[1]);
			glVertex3f(x + sizeX, y + sizeY, z);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
			glVertex3f(x, y + sizeY, z);

			// back face
			glColor4f(color[3].r, color[3].g, color[3].b, color[3].a);
			glTexCoord2f(texCoords[0], texCoords[1]);
			glVertex3f(x, y + sizeY, z + sizeZ);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
			glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y, z + sizeZ);
			glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x, y, z + sizeZ);

			// left face
			glColor4f(color[4].r, color[4].g, color[4].b, color[4].a);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y, z);
			glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y, z + sizeZ);
			glTexCoord2f(texCoords[0], texCoords[1]);
			glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
			glVertex3f(x + sizeX, y + sizeY, z);

			// right face
			glColor4f(color[5].r, color[5].g, color[5].b, color[5].a);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x, y, z + sizeZ);
			glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x, y, z);
			glTexCoord2f(texCoords[0], texCoords[1]);
			glVertex3f(x, y + sizeY, z);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
			glVertex3f(x, y + sizeY, z + sizeZ);
		} else if (texCoords.length > 2) {
			/*
			 * bottom - first top - second front - third back - fourth left -
			 * fifth right - sixth
			 */
			// bottom face (0, 1)
			glColor4f(color[0].r, color[0].g, color[0].b, color[0].a);
			glTexCoord2f(texCoords[0], texCoords[1]);
			glVertex3f(x, y, z + sizeZ);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
			glVertex3f(x + sizeX, y, z + sizeZ);
			glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y, z);
			glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
			glVertex3f(x, y, z);

			// top face (2, 3)
			glColor4f(color[1].r, color[1].g, color[1].b, color[1].a);
			glTexCoord2f(texCoords[2], texCoords[3]);
			glVertex3f(x, y + sizeY, z);
			glTexCoord2f(texCoords[2] + currentSpritesheet.uniformSize(), texCoords[3]);
			glVertex3f(x + sizeX, y + sizeY, z);
			glTexCoord2f(texCoords[2] + currentSpritesheet.uniformSize(), texCoords[3] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
			glTexCoord2f(texCoords[2], texCoords[3] + currentSpritesheet.uniformSize());
			glVertex3f(x, y + sizeY, z + sizeZ);

			// front face (4, 5)
			glColor4f(color[2].r, color[2].g, color[2].b, color[2].a);
			glTexCoord2f(texCoords[4], texCoords[5] + currentSpritesheet.uniformSize());
			glVertex3f(x, y, z);
			glTexCoord2f(texCoords[4] + currentSpritesheet.uniformSize(), texCoords[5] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y, z);
			glTexCoord2f(texCoords[4] + currentSpritesheet.uniformSize(), texCoords[5]);
			glVertex3f(x + sizeX, y + sizeY, z);
			glTexCoord2f(texCoords[4], texCoords[5]);
			glVertex3f(x, y + sizeY, z);

			// back face (6, 7)
			glColor4f(color[3].r, color[3].g, color[3].b, color[3].a);
			glTexCoord2f(texCoords[6], texCoords[7]);
			glVertex3f(x, y + sizeY, z + sizeZ);
			glTexCoord2f(texCoords[6] + currentSpritesheet.uniformSize(), texCoords[7]);
			glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
			glTexCoord2f(texCoords[6] + currentSpritesheet.uniformSize(), texCoords[7] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y, z + sizeZ);
			glTexCoord2f(texCoords[6], texCoords[7] + currentSpritesheet.uniformSize());
			glVertex3f(x, y, z + sizeZ);

			// left face (8, 9)
			glColor4f(color[4].r, color[4].g, color[4].b, color[4].a);
			glTexCoord2f(texCoords[8], texCoords[9] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y, z);
			glTexCoord2f(texCoords[8] + currentSpritesheet.uniformSize(), texCoords[9] + currentSpritesheet.uniformSize());
			glVertex3f(x + sizeX, y, z + sizeZ);
			glTexCoord2f(texCoords[8] + currentSpritesheet.uniformSize(), texCoords[9]);
			glVertex3f(x + sizeX, y + sizeY, z + sizeZ);
			glTexCoord2f(texCoords[8], texCoords[9]);
			glVertex3f(x + sizeX, y + sizeY, z);

			// right face (10, 11)
			glColor4f(color[5].r, color[5].g, color[5].b, color[5].a);
			glTexCoord2f(texCoords[10], texCoords[11] + currentSpritesheet.uniformSize());
			glVertex3f(x, y, z + sizeZ);
			glTexCoord2f(texCoords[10] + currentSpritesheet.uniformSize(), texCoords[11] + currentSpritesheet.uniformSize());
			glVertex3f(x, y, z);
			glTexCoord2f(texCoords[10] + currentSpritesheet.uniformSize(), texCoords[11]);
			glVertex3f(x, y + sizeY, z);
			glTexCoord2f(texCoords[10], texCoords[11]);
			glVertex3f(x, y + sizeY, z + sizeZ);
		}
		}catch(NullPointerException e){
			System.out.println("Error rendering rect: Texcoords null");
			e.printStackTrace();
			System.exit(0);
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Error rendering rect: texcoords/colors wrong size");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void createCross(float x, float y, float z, Color4f color, float[] texCoords, float size) {

		//color = getColor(new Vector3f(x, y, z));

		// face 1
		glColor4f(color.r, color.g, color.b, color.a);
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex3f(x + size, y + size, z + size);
		glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x + size, y, z + size);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x, y, z);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
		glVertex3f(x, y + size, z);

		// face 1 reversed
		glColor4f(color.r, color.g, color.b, color.a);
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex3f(x, y + size, z);
		glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x, y, z);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x + size, y, z + size);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
		glVertex3f(x + size, y + size, z + size);

		// face 2
		glColor4f(color.r, color.g, color.b, color.a);
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex3f(x + size, y + size, z);
		glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x + size, y, z);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x, y, z + size);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
		glVertex3f(x, y + size, z + size);

		// face 2 reversed
		glColor4f(color.r, color.g, color.b, color.a);
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex3f(x, y + size, z + size);
		glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x, y, z + size);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x + size, y, z);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
		glVertex3f(x + size, y + size, z);
	}

	public static void createTexturelessCube(float x, float y, float z, Color4f color, float size) {
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

		glColor4f(1, 1, 1, 1);
	}

	private static Color4f getColor(Vector3f pos) {
		/**
		 * Constants.world.getWorldManager().s.addCurrentTile(1); int progress =
		 * (int) (Constants.world.getWorldManager().s.currentTilePercentage() *
		 * 0.33 + 66);
		 * Constants.world.getWorldManager().s.getSplash().setProgress(progress,
		 * "Lighting chunks "+progress+"%"); float minDist = 7; Color4f color =
		 * new Color4f(0.1f,0.1f,0.1f,1.0f); for(int i = 0; i < lights.size();
		 * i++){ Vector3f lPos = lights.get(i); float dist =
		 * MathUtils.distance(lPos, pos); if(dist <= 7 && dist < minDist || dist
		 * == 0){ float pdist = (7-dist)/7; minDist = dist; color =
		 * MathUtils.mult(new Color4f(1.0f,1.0f,1.0f,1.0f), new
		 * Color4f(pdist,pdist,pdist,1.0f)); } } return color;
		 **/
		/**
		 * float light =
		 * (float)Constants.world.getWorldManager().getLight((int)pos.x,
		 * (int)pos.y, (int)pos.z, false)/(float)7; if(light < 0.2f) light =
		 * 0.2f; return new Color4f(light,light,light,1.0f);
		 **/
		return new Color4f(1f, 1f, 1f, 1f);
	}

	public static void createSquare(float x, float y, Color4f color, float[] texCoords, float size) {
		glColor4f(color.r, color.g, color.b, color.a);
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex2f(x, y);
		glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
		glVertex2f(x, y + size);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
		glVertex2f(x + size, y + size);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
		glVertex2f(x + size, y);
	}
	
	public static void createTexturelessSquare(float x, float y, Color4f color, float size){
		glColor4f(color.r, color.g, color.b, color.a);
		glVertex2f(x, y);
		glVertex2f(x, y + size);
		glVertex2f(x + size, y + size);
		glVertex2f(x + size, y);
	}
	
	public static void createTexturelessRect(float x, float y, float width, float height, Color4f color){
		glColor4f(color.r, color.g, color.b, color.a);
		glVertex2f(x, y);
		glVertex2f(x, y + height);
		glVertex2f(x + width, y + height);
		glVertex2f(x + width, y);
	}

	public static void createCenteredSquare(float x, float y, Color4f color, float[] texCoords, float size) {
		size = size / 2f;
		createSquare(x - size, y - size, color, texCoords, size * 2);
	}

	public static void createPlane(float x, float y, float z, Color4f color, float[] texCoords, float size) {
		// Front
		glColor4f(color.r, color.g, color.b, color.a);
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex3f(x, y, z);
		glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x, y + size, z);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x + size, y + size, z);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
		glVertex3f(x + size, y, z);

		// Back
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
		glVertex3f(x + size, y, z);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x + size, y + size, z);
		glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x, y + size, z);
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex3f(x, y, z);
	}
	
	public static void createFlat(float x, float y, float z, Color4f color, float[] texCoords, float size){
		// Top
		glColor4f(color.r, color.g, color.b, color.a);
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex3f(x, y, z);
		glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x, y, z+size);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x + size, y, z+size);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
		glVertex3f(x + size, y, z);

		// Bottom
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex3f(x + size, y, z);
		glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x + size, y, z+size);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x, y, z+size);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
		glVertex3f(x, y, z);
	}
	
	public static void createSprite(float x, float y, float z, Color4f color, float[] texCoords, float size){ //one sided plane
		glColor4f(color.r, color.g, color.b, color.a);
		glTexCoord2f(texCoords[0], texCoords[1]);
		glVertex3f(x, y, z);
		glTexCoord2f(texCoords[0], texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x, y + size, z);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1] + currentSpritesheet.uniformSize());
		glVertex3f(x + size, y + size, z);
		glTexCoord2f(texCoords[0] + currentSpritesheet.uniformSize(), texCoords[1]);
		glVertex3f(x + size, y, z);
	}

	public static void createTexturelessFlat(float x, float y, float z, Color4f color, float size) {
		// Top
		glColor4f(color.r, color.g, color.b, color.a);
		glVertex3f(x, y, z);
		glVertex3f(x, y, z+size);
		glVertex3f(x + size, y, z+size);
		glVertex3f(x + size, y, z);

		// Bottom
		glVertex3f(x + size, y, z);
		glVertex3f(x + size, y, z+size);
		glVertex3f(x, y, z+size);
		glVertex3f(x, y, z);
	}

	public static void createTexturelessCross(float x, float y, float z, Color4f color, float size) {
		//color = getColor(new Vector3f(x, y, z));

		// face 1
		glColor4f(color.r, color.g, color.b, color.a);
		glVertex3f(x + size, y + size, z + size);
		glVertex3f(x + size, y, z + size);
		glVertex3f(x, y, z);
		glVertex3f(x, y + size, z);

		// face 1 reversed
		glColor4f(color.r, color.g, color.b, color.a);
		glVertex3f(x, y + size, z);
		glVertex3f(x, y, z);
		glVertex3f(x + size, y, z + size);
		glVertex3f(x + size, y + size, z + size);

		// face 2
		glColor4f(color.r, color.g, color.b, color.a);
		glVertex3f(x + size, y + size, z);
		glVertex3f(x + size, y, z);
		glVertex3f(x, y, z + size);
		glVertex3f(x, y + size, z + size);

		// face 2 reversed
		glColor4f(color.r, color.g, color.b, color.a);;
		glVertex3f(x, y + size, z + size);
		glVertex3f(x, y, z + size);
		glVertex3f(x + size, y, z);
		glVertex3f(x + size, y + size, z);
	}
}
