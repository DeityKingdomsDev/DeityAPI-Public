package com.imdeity.deityapi.object;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntitySmallFireball;

import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.imdeity.deityapi.DeityAPI;

/**
 * API calls to deal with effects in the world
 * 
 * @author vanZeben
 */
@SuppressWarnings("deprecation")
public class EffectObject {
    
    /**
     * Task to un-vanish a player
     * 
     * @author vanZeben
     */
    public class UnVanishTask implements Runnable {
        private Player player;
        
        public UnVanishTask(Player player) {
            this.player = player;
        }
        
        @Override
        public void run() {
            try {
                if (this.player.isOnline()) {
                    DeityAPI.getAPI().getEffectAPI().unapplyVanish(this.player);
                }
            } catch (Exception ex) {
            }
        }
    }
    
    public static HashMap<String, Integer> vanishedPlayers = new HashMap<String, Integer>();
    
    /**
     * Applies the Blindness potion effect
     * 
     * @param player
     *            Player to apply effect to
     * @param duration
     *            How long (in seconds) to apply the effect
     * @param strength
     *            How strong the effect should be
     */
    public void applyBlindness(Player player, int duration, int strength) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration * 20, strength));
        this.showSmoke(player.getLocation());
    }
    
    /**
     * Applies the Confusion potion effect
     * 
     * @param player
     *            Player to apply effect to
     * @param duration
     *            How long (in seconds) to apply the effect
     * @param strength
     *            How strong the effect should be
     */
    public void applyConfusion(Player player, int duration, int strength) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration * 20, strength));
        this.showSmoke(player.getLocation());
    }
    
    /**
     * Applies the Jump potion effect
     * 
     * @param player
     *            Player to apply effect to
     * @param duration
     *            How long (in seconds) to apply the effect
     * @param strength
     *            How strong the effect should be
     */
    public void applyJump(Player player, int duration, int strength) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration * 20, strength));
        this.showSmoke(player.getLocation());
    }
    
    /**
     * Applies the Night Vision potion effect
     * 
     * @param player
     *            Player to apply effect to
     * @param duration
     *            How long (in seconds) to apply the effect
     * @param strength
     *            How strong the effect should be
     */
    public void applyNightVision(Player player, int duration, int strength) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, duration * 20, strength));
        this.showSmoke(player.getLocation());
    }
    
    /**
     * Applies the Speed potion effect
     * 
     * @param player
     *            Player to apply effect to
     * @param duration
     *            How long (in seconds) to apply the effect
     * @param strength
     *            How strong the effect should be
     */
    public void applySpeed(Player player, int duration, int strength) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, strength));
        this.showSmoke(player.getLocation());
    }
    
    /**
     * Applies the Vanish potion effect
     * 
     * @param player
     *            Player to apply effect to
     * @param duration
     *            How long (in seconds) to apply the effect
     * @param strength
     *            How strong the effect should be
     */
    public void applyVanish(Player player, int duration, int strength) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration * 20, strength), true);
        this.showSmoke(player.getLocation());
        this.updateVanished(player);
        EffectObject.vanishedPlayers.put(
                player.getName(),
                DeityAPI.plugin.getServer().getScheduler()
                        .scheduleAsyncDelayedTask(DeityAPI.plugin, new UnVanishTask(player), duration * 20));
    }
    
    /**
     * Applies the Water-Breathing potion effect
     * 
     * @param player
     *            Player to apply effect to
     * @param duration
     *            How long (in seconds) to apply the effect
     * @param strength
     *            How strong the effect should be
     */
    public void applyWaterBreathing(Player player, int duration, int strength) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, duration * 20, strength));
        this.showSmoke(player.getLocation());
    }
    
    /**
     * Shoots a large fireball into the world
     * 
     * @param world
     *            World to shoot in
     * @param originCoords
     *            {x, y, z} coords of the origin
     * @param targetCoords
     *            {x, y, z} coords of the target
     */
    private void shootLargeFireBall(CraftWorld world, int[] originCoords, int[] targetCoords) {
        double[] slopevector = new double[3];
        double linelength = 0.0D;
        
        for (int i = 0; i < 3; i++) {
            slopevector[i] = (targetCoords[i] - originCoords[i]);
        }
        linelength = Math.pow(Math.pow(slopevector[0], 2.0D) + Math.pow(slopevector[1], 2.0D) + Math.pow(slopevector[2], 2.0D), 0.5D);
        for (int i = 0; i < 3; i++) {
            slopevector[i] /= linelength;
        }
        EntityFireball entityFireball = new EntityFireball(world.getHandle(), originCoords[0], originCoords[1], originCoords[2],
                slopevector[0] * linelength, slopevector[1] * linelength, slopevector[2] * linelength);
        world.getHandle().addEntity(entityFireball);
    }
    
    /**
     * Shoots a large fireball at a location
     * 
     * @param locationFrom
     *            fireball's origin
     * @param locationTo
     *            fireball's target
     */
    public void shootLargeFireballAtLocation(Location locationFrom, Location locationTo) {
        int[] originCoords = new int[3];
        int[] targetCoords = new int[3];
        
        originCoords[0] = (int) locationFrom.getX();
        originCoords[1] = (int) locationFrom.getY();
        originCoords[2] = (int) locationFrom.getZ();
        
        targetCoords[0] = (int) (locationTo.getX() + 0.5D * locationTo.getX() / Math.abs(locationTo.getX()));
        targetCoords[1] = (int) (locationTo.getY() + 0.5D);
        targetCoords[2] = (int) (locationTo.getZ() + 0.5D * locationTo.getZ() / Math.abs(locationTo.getZ()));
        this.shootLargeFireBall(((CraftWorld) locationFrom.getWorld()), originCoords, targetCoords);
    }
    
    /**
     * Shoots a small fireball into the world
     * 
     * @param world
     *            World to shoot in
     * @param originCoords
     *            {x, y, z} coords of the origin
     * @param targetCoords
     *            {x, y, z} coords of the target
     */
    private void shootSmallFireBall(CraftWorld world, int[] originCoords, int[] targetCoords) {
        double[] slopevector = new double[3];
        double linelength = 0.0D;
        
        for (int i = 0; i < 3; i++) {
            slopevector[i] = (targetCoords[i] - originCoords[i]);
        }
        linelength = Math.pow(Math.pow(slopevector[0], 2.0D) + Math.pow(slopevector[1], 2.0D) + Math.pow(slopevector[2], 2.0D), 0.5D);
        for (int i = 0; i < 3; i++) {
            slopevector[i] /= linelength;
        }
        EntitySmallFireball entitySmallFireball = new EntitySmallFireball(world.getHandle(), originCoords[0], originCoords[1],
                originCoords[2], slopevector[0] * linelength, slopevector[1] * linelength, slopevector[2] * linelength);
        world.getHandle().addEntity(entitySmallFireball);
    }
    
    /**
     * Shoots a small fireball at a location
     * 
     * @param locationFrom
     *            fireball's origin
     * @param locationTo
     *            fireball's target
     */
    public void shootSmallFireballAtLocation(Location locationFrom, Location locationTo) {
        int[] originCoords = new int[3];
        int[] targetCoords = new int[3];
        
        originCoords[0] = (int) locationFrom.getX();
        originCoords[1] = (int) locationFrom.getY();
        originCoords[2] = (int) locationFrom.getZ();
        
        targetCoords[0] = (int) (locationTo.getX() + 0.5D * locationTo.getX() / Math.abs(locationTo.getX()));
        targetCoords[1] = (int) (locationTo.getY() + 0.5D);
        targetCoords[2] = (int) (locationTo.getZ() + 0.5D * locationTo.getZ() / Math.abs(locationTo.getZ()));
        this.shootSmallFireBall(((CraftWorld) locationFrom.getWorld()), originCoords, targetCoords);
    }
    
    /**
     * Shows the heart effect on a player
     * 
     * @param player
     */
    public void showHearts(Player player) {
        for (int i = 0; i < 10; i++) {
            player.playEffect(EntityEffect.WOLF_HEARTS);
        }
    }
    
    /**
     * Shows a smoke burst at a location
     * 
     * @param location
     */
    public void showSmoke(Location location) {
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            location.getWorld().playEffect(location, Effect.SMOKE, random.nextInt(9));
        }
    }
    
    /**
     * Unapplies the Blindness potion effect
     * 
     * @param player
     */
    public void unapplyBlindness(Player player) {
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }
    
    /**
     * Unapplies the Confusion potion effect
     * 
     * @param player
     */
    public void unapplyConfusion(Player player) {
        player.removePotionEffect(PotionEffectType.CONFUSION);
    }
    
    /**
     * Unapplies the Jump potion effect
     * 
     * @param player
     */
    public void unapplyJump(Player player) {
        player.removePotionEffect(PotionEffectType.JUMP);
    }
    
    /**
     * Unapplies the Night Vision potion effect
     * 
     * @param player
     */
    public void unapplyNightVision(Player player) {
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
    
    /**
     * Unapplies the Speed potion effect
     * 
     * @param player
     */
    public void unapplySpeed(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
    }
    
    /**
     * Unapplies the Vanish potion effect
     * 
     * @param player
     */
    public void unapplyVanish(Player player) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        this.applyVanish(player, 1, 0);
    }
    
    /**
     * Unapplies the Water-Breathing potion effect
     * 
     * @param player
     */
    public void unapplyWaterBreathing(Player player) {
        player.removePotionEffect(PotionEffectType.WATER_BREATHING);
    }
    
    /**
     * Updates vanished players
     * 
     * @param player
     */
    private void updateVanished(Player player) {
        if (EffectObject.vanishedPlayers.containsKey(player.getName())) {
            for (Player other : DeityAPI.plugin.getServer().getOnlinePlayers()) {
                if (!other.getName().equals(player.getName())) {
                    if (other.canSee(player)) {
                        other.hidePlayer(player);
                    }
                }
            }
            DeityAPI.plugin.getServer().getScheduler().cancelTask(EffectObject.vanishedPlayers.get(player.getName()));
        } else {
            for (Player other : DeityAPI.plugin.getServer().getOnlinePlayers()) {
                if (!other.equals(player)) {
                    if (!other.canSee(player)) {
                        other.showPlayer(player);
                    }
                }
            }
        }
    }
    
    /**
     * Updates specific vanished players
     * 
     * @param player
     * @param other
     */
    public void updateVanished(Player player, Player other) {
        if (EffectObject.vanishedPlayers.containsKey(player.getName())) {
            if (other.canSee(player)) {
                other.hidePlayer(player);
            }
        } else {
            if (!other.canSee(player)) {
                other.showPlayer(player);
            }
        }
    }
    
    public void createExplosion(Location location, int size) {
        location.getWorld().createExplosion(location, size, false);
    }
}
