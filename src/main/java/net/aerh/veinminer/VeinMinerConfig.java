package net.aerh.veinminer;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class VeinMinerConfig {

    private boolean enabled;
    private int veinMineRadius;
    private long cooldown;
    private final List<Material> allowedMaterials;
    private final List<Material> allowedTools;

    public VeinMinerConfig(FileConfiguration config) {
        this.enabled = config.getBoolean("enabled", true);
        this.veinMineRadius = Math.min(config.getInt("max-radius", 5), 100);
        this.cooldown = config.getLong("cooldown", 1_000L);
        this.allowedMaterials = new ArrayList<>();
        this.allowedTools = new ArrayList<>();

        config.getStringList("allowed-materials")
                .stream()
                .map(s -> Material.matchMaterial(s.toUpperCase()))
                .forEach(this.allowedMaterials::add);

        config.getStringList("allowed-tools")
                .stream()
                .map(s -> Material.matchMaterial(s.toUpperCase()))
                .forEach(this.allowedTools::add);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getVeinMineRadius() {
        return veinMineRadius;
    }

    public void setVeinMineRadius(int veinMineRadius) {
        this.veinMineRadius = veinMineRadius;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public List<Material> getAllowedMaterials() {
        return allowedMaterials;
    }

    public List<Material> getAllowedTools() {
        return allowedTools;
    }
}
