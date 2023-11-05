package net.aerh.veinminer;

import org.bukkit.plugin.java.JavaPlugin;

public final class VeinMinerPlugin extends JavaPlugin {

    private VeinMinerConfig veinMinerConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        veinMinerConfig = new VeinMinerConfig(getConfig());

        getServer().getPluginManager().registerEvents(new VeinMinerListener(this), this);
        getCommand("veinminer").setExecutor(new VeinMinerCommand(this));
    }

    @Override
    public void onDisable() {
        veinMinerConfig.getAllowedTools().clear();
        veinMinerConfig.getAllowedMaterials().clear();
    }

    public VeinMinerConfig getVeinMinerConfig() {
        return veinMinerConfig;
    }

    public void setVeinMinerConfig(VeinMinerConfig veinMinerConfig) {
        this.veinMinerConfig = veinMinerConfig;
    }
}
