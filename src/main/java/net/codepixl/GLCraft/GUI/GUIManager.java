package net.codepixl.GLCraft.GUI;

import com.nishu.utils.Color4f;
import net.codepixl.GLCraft.GLCraft;
import net.codepixl.GLCraft.GUI.Elements.GUIButton;
import net.codepixl.GLCraft.GUI.Elements.GUISlider;
import net.codepixl.GLCraft.GUI.Elements.GUITextBox;
import net.codepixl.GLCraft.GUI.Inventory.Elements.GUISlot;
import net.codepixl.GLCraft.GUI.Inventory.GUIInventoryScreen;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import java.util.Stack;

public class GUIManager {

    private GUIScreen currentGUI;
    private boolean GUIOpen = false, showGame = false;
    private static GUIManager mainManager;
    private GUITextBox focusedTextBox;
    private GUIGame gameGUI;
    private Stack<GUIScreen> guiStack = new Stack<GUIScreen>();

    public GUIManager(){
        initTextures();
    }
    
    private void initTextures(){
        TextureManager.addTexture("gui.guislot", TextureManager.GUIS+"guislot.png");
        TextureManager.addTexture("gui.heart", TextureManager.GUIS+"heart.png");
        TextureManager.addTexture("gui.heart_half", TextureManager.GUIS+"heart_half.png");
        TextureManager.addTexture("gui.heart_empty", TextureManager.GUIS+"heart_empty.png");
        TextureManager.addTexture("gui.bubble", TextureManager.GUIS+"bubble.png");
    }
    
    public void setShowGame(boolean show){
        showGame = show;
    }

    public void setGameGUI(GUIGame gui){
        this.gameGUI = gui;
    }
    
    public GUIGame getGameGUI(){
        return this.gameGUI;
    }
    
    public static void setMainManager(GUIManager manager){
        mainManager = manager;
    }
    
    public static GUIManager getMainManager(){
        return mainManager;
    }

    public void showGUI(GUIScreen gui){
        if(currentGUI != null)
            guiStack.add(currentGUI);
        currentGUI = gui;
        gui.makeElements();
        gui.onOpen();
        GUIOpen = true;
    }
    
    public boolean isGUIOpen(){
        return GUIOpen;
    }

    public void closeGUI(boolean onClose){
        GUIScreen tmp = currentGUI;
        if(!guiStack.isEmpty())
            currentGUI = guiStack.pop();
        else{
            GUIOpen = false;
            currentGUI = null;
        }
        focusedTextBox = null;
        if(tmp != null && onClose)
            tmp.onClose();
    }
    
    public void clearGUIStack(){
        guiStack.clear();
    }
    
    public GUIScreen getCurrentGUI(){
        return currentGUI;
    }

    public void render() {
        if(showGame)
            gameGUI.renderMain();
        if (GUIOpen)
            currentGUI.renderMain();
        renderMouseItem();
        EntityPlayer p = GLCraft.getGLCraft().getEntityManager(false).getPlayer();
        if(p.mouseItem.isNull() && p.hoverSlot != null && p.hoverSlot.showLabel && p.hoverSlot.hover && !p.hoverSlot.itemstack.isNull()){
            TextureImpl.unbind();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBegin(GL11.GL_QUADS);
            Shape.createTexturelessRect2D(Mouse.getX(), -Mouse.getY()+Constants.getHeight()-Tesselator.getFontHeight(), Tesselator.getFontWidth(p.hoverSlot.itemstack.getName())+4, Tesselator.getFontHeight()+4, new Color4f(0f, 0f, 0f, 0.5f));
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            Tesselator.drawTextWithShadow(Mouse.getX()+2, -Mouse.getY()+Constants.getHeight()-Tesselator.getFontHeight()+2, p.hoverSlot.itemstack.getName());
        }
    }

    public void update(){
        if(Display.wasResized())
            this.resize();
        if(showGame)
            gameGUI.update();
        if (GUIOpen)
            currentGUI.update();
    }

    public void input(){
        EntityPlayer p = GLCraft.getGLCraft().getEntityManager(false).getPlayer();
        p.hoverSlot = null;
        if (GUIOpen){
            currentGUI.input(0,0);
            if(currentGUI instanceof GUIInventoryScreen)
                gameGUI.input(0,0);
        }else if(showGame)
            gameGUI.input(0, 0);
        if(focusedTextBox != null){
            Keyboard.enableRepeatEvents(true);
            while(Keyboard.next()){
                if(Keyboard.getEventKeyState()){
                    char c = Keyboard.getEventCharacter();
                    int k = Keyboard.getEventKey();
                    if(k == Keyboard.KEY_ESCAPE){
                        this.focusedTextBox.setFocused(false);
                        if(this.focusedTextBox.closeOnUnfocus)
                            closeGUI(true);
                        this.focusedTextBox = null;
                    }else{
                        this.focusedTextBox.textInput(k,c);
                    }
                }
            }
            Keyboard.enableRepeatEvents(false);
        }
    }
    
    public void renderMouseItem(){
        if(this.currentGUI != null && this.currentGUI.shouldRenderMouseItem()){
            EntityPlayer player = GLCraft.getGLCraft().getEntityManager(false).getPlayer();
            player.mouseItem.renderIcon(Mouse.getX(), -Mouse.getY()+Constants.getHeight(), 64);
        }
    }

    public boolean mouseShouldBeGrabbed(){
        if(currentGUI != null && GUIOpen){
            return currentGUI.mouseGrabbed;
        }else{
            return true;
        }
    }
    
    public boolean sendPlayerInput(){
        if(GUIOpen){
            return currentGUI.playerInput;
        }else{
            return true;
        }
    }

    public void setFocusedTextBox(GUITextBox textBox){
        this.focusedTextBox = textBox;
        textBox.setFocused(true);
    }
    
    public void unfocusTextBox(){
        if(this.focusedTextBox != null){
            this.focusedTextBox.setFocused(false);
            if(this.focusedTextBox.closeOnUnfocus)
                closeGUI(true);
        }
        this.focusedTextBox = null;
    }

    public void resize(){
        if(currentGUI != null) currentGUI.didResize = true;
        for(GUIScreen g : guiStack) g.didResize = true;
        this.gameGUI.didResize = true;
        if(Constants.getWidth() <= 1000)
            GUISlot.size = Constants.getWidth()/18f;
        else
            GUISlot.size = Constants.getWidth()/25f;
        GUIButton.BTNHEIGHT = 30*Constants.getGUIScale();
        GUIButton.BTNPADDING = 10*Constants.getGUIScale();
        GUISlider.HEIGHT = 20*Constants.getGUIScale();
        Tesselator.initFont();
    }
}
