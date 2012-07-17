package com.imdeity.deityapi.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;

/**
 * Alternate names for bukkit enchantments
 * 
 * @author vanZeben
 */
public class Enchantments {
    private static final transient Pattern NUMPATTERN = Pattern.compile("\\d+");
    private static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<String, Enchantment>();
    
    static {
        Enchantments.ENCHANTMENTS.put("alldamage", Enchantment.DAMAGE_ALL);
        Enchantments.ENCHANTMENTS.put("alldmg", Enchantment.DAMAGE_ALL);
        Enchantments.ENCHANTMENTS.put("sharpness", Enchantment.DAMAGE_ALL);
        Enchantments.ENCHANTMENTS.put("arthropodsdamage", Enchantment.DAMAGE_ARTHROPODS);
        Enchantments.ENCHANTMENTS.put("ardmg", Enchantment.DAMAGE_ARTHROPODS);
        Enchantments.ENCHANTMENTS.put("baneofarthropods", Enchantment.DAMAGE_ARTHROPODS);
        Enchantments.ENCHANTMENTS.put("undeaddamage", Enchantment.DAMAGE_UNDEAD);
        Enchantments.ENCHANTMENTS.put("smite", Enchantment.DAMAGE_UNDEAD);
        Enchantments.ENCHANTMENTS.put("digspeed", Enchantment.DIG_SPEED);
        Enchantments.ENCHANTMENTS.put("efficiency", Enchantment.DIG_SPEED);
        Enchantments.ENCHANTMENTS.put("durability", Enchantment.DURABILITY);
        Enchantments.ENCHANTMENTS.put("dura", Enchantment.DURABILITY);
        Enchantments.ENCHANTMENTS.put("unbreaking", Enchantment.DURABILITY);
        Enchantments.ENCHANTMENTS.put("fireaspect", Enchantment.FIRE_ASPECT);
        Enchantments.ENCHANTMENTS.put("fire", Enchantment.FIRE_ASPECT);
        Enchantments.ENCHANTMENTS.put("knockback", Enchantment.KNOCKBACK);
        Enchantments.ENCHANTMENTS.put("blockslootbonus", Enchantment.LOOT_BONUS_BLOCKS);
        Enchantments.ENCHANTMENTS.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
        Enchantments.ENCHANTMENTS.put("mobslootbonus", Enchantment.LOOT_BONUS_MOBS);
        Enchantments.ENCHANTMENTS.put("mobloot", Enchantment.LOOT_BONUS_MOBS);
        Enchantments.ENCHANTMENTS.put("looting", Enchantment.LOOT_BONUS_MOBS);
        Enchantments.ENCHANTMENTS.put("oxygen", Enchantment.OXYGEN);
        Enchantments.ENCHANTMENTS.put("respiration", Enchantment.OXYGEN);
        Enchantments.ENCHANTMENTS.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
        Enchantments.ENCHANTMENTS.put("prot", Enchantment.PROTECTION_ENVIRONMENTAL);
        Enchantments.ENCHANTMENTS.put("explosionsprotection", Enchantment.PROTECTION_EXPLOSIONS);
        Enchantments.ENCHANTMENTS.put("expprot", Enchantment.PROTECTION_EXPLOSIONS);
        Enchantments.ENCHANTMENTS.put("blastprotection", Enchantment.PROTECTION_EXPLOSIONS);
        Enchantments.ENCHANTMENTS.put("fallprotection", Enchantment.PROTECTION_FALL);
        Enchantments.ENCHANTMENTS.put("fallprot", Enchantment.PROTECTION_FALL);
        Enchantments.ENCHANTMENTS.put("featherfalling", Enchantment.PROTECTION_FALL);
        Enchantments.ENCHANTMENTS.put("fireprotection", Enchantment.PROTECTION_FIRE);
        Enchantments.ENCHANTMENTS.put("fireprot", Enchantment.PROTECTION_FIRE);
        Enchantments.ENCHANTMENTS.put("projectileprotection", Enchantment.PROTECTION_PROJECTILE);
        Enchantments.ENCHANTMENTS.put("projprot", Enchantment.PROTECTION_PROJECTILE);
        Enchantments.ENCHANTMENTS.put("silktouch", Enchantment.SILK_TOUCH);
        Enchantments.ENCHANTMENTS.put("waterworker", Enchantment.WATER_WORKER);
        Enchantments.ENCHANTMENTS.put("aquaaffinity", Enchantment.WATER_WORKER);
        Enchantments.ENCHANTMENTS.put("arrowdamage", Enchantment.ARROW_DAMAGE);
        Enchantments.ENCHANTMENTS.put("arrowfire", Enchantment.ARROW_FIRE);
        Enchantments.ENCHANTMENTS.put("arrowinfinite", Enchantment.ARROW_INFINITE);
        Enchantments.ENCHANTMENTS.put("arrowknockback", Enchantment.ARROW_KNOCKBACK);
    }
    
    public static Set<Entry<String, Enchantment>> entrySet() {
        return Enchantments.ENCHANTMENTS.entrySet();
    }
    
    public static Enchantment getByName(String name) {
        Enchantment enchantment;
        if (Enchantments.NUMPATTERN.matcher(name).matches()) {
            enchantment = Enchantment.getById(Integer.parseInt(name));
        } else {
            enchantment = Enchantment.getByName(name.toUpperCase(Locale.ENGLISH));
        }
        if (enchantment == null) {
            enchantment = Enchantments.ENCHANTMENTS.get(name.toLowerCase(Locale.ENGLISH));
        }
        return enchantment;
    }
}