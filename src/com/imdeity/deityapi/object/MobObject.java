package com.imdeity.deityapi.object;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;

import com.imdeity.deityapi.DeityAPI;

/**
 * API to deal with mobs
 * 
 * @author vanZeben
 */
public class MobObject {
    
    /**
     * Despawns a specific mob
     * 
     * @author vanZeben
     */
    public class DeSpawner implements Runnable {
        
        private int id;
        private World world;
        
        public DeSpawner(int id, World world) {
            this.id = id;
            this.world = world;
            DeityAPI.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(DeityAPI.plugin, this);
        }
        
        @Override
        public void run() {
            try {
                for (Entity e : this.world.getEntities()) {
                    if (e.getEntityId() == this.id) {
                        e.remove();
                        break;
                    }
                }
            } catch (Exception ex) {
            }
        }
    }
    
    /**
     * Spawns a specific mob type
     * 
     * @author vanZeben
     */
    public class Spawner implements Runnable {
        
        private int amount = 0;
        private ArrayList<Integer> entityIds = new ArrayList<Integer>();
        private Location location = null;
        private EntityType type = null;
        
        public Spawner(EntityType type, int amount, Location location) {
            this.type = type;
            this.amount = amount;
            this.location = location;
            this.run();
        }
        
        public ArrayList<Integer> getIds() {
            return this.entityIds;
        }
        
        @Override
        public void run() {
            try {
                for (int i = 0; i < this.amount; i++) {
                    if (this.type != null) {
                        LivingEntity entity = (LivingEntity) this.location.getWorld().spawnEntity(this.location, this.type);
                        this.entityIds.add(entity.getEntityId());
                    }
                }
            } catch (Exception ex) {
            }
        }
    }
    
    /**
     * Returns the EntityType parsed from the name
     * 
     * @param name
     * @return
     */
    public EntityType getEntityType(String name) {
        if ((name == null) || name.equalsIgnoreCase("")) {
            return null;
        } else if (name.equalsIgnoreCase("blaze")) {
            return EntityType.BLAZE;
        } else if (name.equalsIgnoreCase("cavespider")) {
            return EntityType.CAVE_SPIDER;
        } else if (name.equalsIgnoreCase("creeper")) {
            return EntityType.CREEPER;
        } else if (name.equalsIgnoreCase("enderman")) {
            return EntityType.ENDERMAN;
        } else if (name.equalsIgnoreCase("ghast")) {
            return EntityType.GHAST;
        } else if (name.equalsIgnoreCase("magmacube")) {
            return EntityType.MAGMA_CUBE;
        } else if (name.equalsIgnoreCase("pigzombie")) {
            return EntityType.PIG_ZOMBIE;
        } else if (name.equalsIgnoreCase("silverfish")) {
            return EntityType.SILVERFISH;
        } else if (name.equalsIgnoreCase("skeleton")) {
            return EntityType.SKELETON;
        } else if (name.equalsIgnoreCase("spider")) {
            return EntityType.SPIDER;
        } else if (name.equalsIgnoreCase("wolf")) {
            return EntityType.WOLF;
        } else if (name.equalsIgnoreCase("zombie")) { return EntityType.ZOMBIE; }
        return null;
    }
    
    /**
     * Returns the mob name from the Entity
     * 
     * @param entity
     * @return
     */
    public String getMobName(Entity entity) {
        if (entity instanceof Blaze) {
            return "blaze";
        } else if (entity instanceof CaveSpider) {
            return "cavespider";
        } else if (entity instanceof Creeper) {
            return "creeper";
        } else if (entity instanceof Enderman) {
            return "enderman";
        } else if (entity instanceof Ghast) {
            return "ghast";
        } else if (entity instanceof MagmaCube) {
            return "magmacube";
        } else if (entity instanceof PigZombie) {
            return "pigzombie";
        } else if (entity instanceof Silverfish) {
            return "silverfish";
        } else if (entity instanceof Skeleton) {
            return "skeleton";
        } else if (entity instanceof Spider) {
            return "spider";
        } else if (entity instanceof Wolf) {
            return "wolf";
        } else if (entity instanceof Zombie) { return "zombie"; }
        return null;
    }
    
    public net.minecraft.server.PathPoint getPathPoint(int x, int y, int z) {
        return new net.minecraft.server.PathPoint(x, y, z);
    }
    
    public void moveCreatureToMultiplePoint(CraftCreature entity, net.minecraft.server.PathPoint[] points) {
        entity.getHandle().setPathEntity(new net.minecraft.server.PathEntity(points));
    }
    
    public void moveCreatureToSinglePoint(CraftCreature entity, int x, int y, int z) {
        entity.getHandle().setPathEntity(new net.minecraft.server.PathEntity(new net.minecraft.server.PathPoint[] { new net.minecraft.server.PathPoint(x, y, z) }));
    }
    
    /**
     * Schedules a despawn of the entity specified
     * 
     * @param entityId
     * @param world
     */
    public void despawnMob(int entityId, World world) {
        new DeSpawner(entityId, world);
    }
    
    /**
     * Schedules a spawn of the specified entity
     * 
     * @param type
     * @param amount
     * @param location
     * @return
     */
    public ArrayList<Integer> spawnMobs(EntityType type, int amount, Location location) {
        Spawner spawner = new Spawner(type, amount, location);
        return spawner.getIds();
    }
    
    /**
     * Schedules a spawn of a mob
     * 
     * @param type
     * @param location
     * @return
     */
    public ArrayList<Integer> spawnMob(EntityType type, Location location) {
        Spawner spawner = new Spawner(type, 1, location);
        return spawner.getIds();
    }
    
}
