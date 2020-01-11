package net.codepixl.GLCraft.util.command;

import net.codepixl.GLCraft.util.ChatFormat;
import net.codepixl.GLCraft.world.CentralManager;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityManager;
import net.codepixl.GLCraft.world.entity.mob.EntityPlayer;
import net.codepixl.GLCraft.world.entity.mob.Mob;
import net.codepixl.GLCraft.world.entity.tileentity.TileEntity;
import org.lwjgl.util.vector.Vector3f;

import java.lang.reflect.Constructor;

public class CommandSpawn implements Command {

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public boolean execute(CentralManager centralManager, CommandExecutor e, String... args) {
        if(e.getType() != CommandExecutor.PLAYER) {
            e.sendMessage(ChatFormat.RED + "Must be run by player");
            return true;
        }
        EntityPlayer player = (EntityPlayer) e;
        try {
            if(args.length == 2) {
                Class<? extends Entity> entityClass = EntityManager.getRegisteredEntity(args[1]);
                if(entityClass != null) {
                    if(EntityPlayer.class.isAssignableFrom(entityClass) || TileEntity.class.isAssignableFrom(entityClass)) {
                        e.sendMessage(ChatFormat.RED + "Cannot spawn that kind of entity");
                    } else {
                        Entity entity;
                        Constructor<? extends Entity> constructor;
                        if(Mob.class.isAssignableFrom(entityClass)) {
                            constructor = entityClass.getConstructor(Vector3f.class, WorldManager.class);
                            entity = constructor.newInstance(player.getPos(), player.worldManager);
                        } else {
                            constructor = entityClass.getConstructor(Vector3f.class, Vector3f.class, Vector3f.class, WorldManager.class);
                            entity = constructor.newInstance(player.getPos(), new Vector3f(), new Vector3f(), player.worldManager);
                        }
                        player.worldManager.spawnEntity(entity);
                        e.sendMessage(ChatFormat.GREEN + "Entity spawned");
                    }
                } else {
                    e.sendMessage(ChatFormat.RED + "Entity not found");
                }
                return true;
            }
        } catch(Exception ex) {
            e.sendMessage(ChatFormat.RED + "Unable to spawn entity");
            ex.printStackTrace();
            return true;
        }
        return false;
    }

    @Override
    public Permission getPermission() {
        return Permission.OP;
    }

    @Override
    public String getUsage() {
        return "<entity name> - Spawn an entity.";
    }

}