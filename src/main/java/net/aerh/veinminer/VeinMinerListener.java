package net.aerh.veinminer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class VeinMinerListener implements Listener {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");
    private static final BlockFace[] BLOCK_FACES = BlockFace.values();

    private final Map<UUID, Long> cooldowns;
    private final VeinMinerPlugin plugin;

    public VeinMinerListener(VeinMinerPlugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Material heldItem = player.getInventory().getItemInMainHand().getType();

        if (!plugin.getVeinMinerConfig().isEnabled()) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        if (canVeinMine(player, heldItem) && isValidBlock(block.getType())) {
            if (isInCooldown(player)) {
                double remainingCooldown = calculateRemainingCooldown(player);
                player.sendMessage(ChatColor.RED + "You must wait " + formatNumber(remainingCooldown) + "s before activating vein miner!");
                return;
            }

            cooldowns.remove(player.getUniqueId());
            List<Block> connected = findConnectedBlocks(block, plugin.getVeinMinerConfig().getVeinMineRadius());

            long delay = 0L;
            for (Block block1 : connected) {
                delay += 3L + connected.indexOf(block1);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    block1.breakNaturally(player.getInventory().getItemInMainHand());
                    block1.getWorld().spawnParticle(Particle.CLOUD, block1.getLocation().add(0.5, 0.5, 0.5), 1, 0, 0, 0, 0);
                    block1.getWorld().playSound(block1.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1F, 1F + (connected.indexOf(block1) / 5F));
                }, delay);
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.sendMessage(ChatColor.YELLOW + "You vein mined " + ChatColor.GREEN + connected.size()
                        + " block" + (connected.size() == 1 ? "" : "s") + ChatColor.YELLOW + "!");
            }, delay);

            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    public List<Block> findConnectedBlocks(Block startBlock, int limit) {
        Material targetMaterial = startBlock.getType();
        List<Block> connectedBlocks = new ArrayList<>();
        List<Block> checkedBlocks = new ArrayList<>();
        List<Block> blocksToCheck = new ArrayList<>();

        blocksToCheck.add(startBlock);

        while (!blocksToCheck.isEmpty() && connectedBlocks.size() < limit) {
            Block currentBlock = blocksToCheck.remove(0);

            for (BlockFace face : BLOCK_FACES) {
                Block adjacentBlock = currentBlock.getRelative(face);

                if (adjacentBlock.getType() == Material.AIR) {
                    continue;
                }

                if (adjacentBlock.getType() == targetMaterial && !checkedBlocks.contains(adjacentBlock)) {
                    connectedBlocks.add(adjacentBlock);
                    checkedBlocks.add(adjacentBlock);
                    blocksToCheck.add(adjacentBlock);

                    if (connectedBlocks.size() >= limit) {
                        return connectedBlocks;
                    }
                }
            }
        }

        return connectedBlocks;
    }

    private boolean isValidBlock(Material material) {
        return plugin.getVeinMinerConfig().getAllowedMaterials().contains(material);
    }

    private boolean canVeinMine(Player player, Material material) {
        return player.hasPermission("veinminer.use") && plugin.getVeinMinerConfig().getAllowedTools().contains(material);
    }

    private boolean isInCooldown(Player player) {
        return cooldowns.containsKey(player.getUniqueId())
                && System.currentTimeMillis() - cooldowns.get(player.getUniqueId()) < plugin.getVeinMinerConfig().getCooldown();
    }

    private double calculateRemainingCooldown(Player player) {
        long currentTime = System.currentTimeMillis();
        return (cooldowns.get(player.getUniqueId()) + plugin.getVeinMinerConfig().getCooldown() - currentTime) / 1_000D;
    }

    private String formatNumber(double number) {
        return DECIMAL_FORMAT.format(number);
    }
}
