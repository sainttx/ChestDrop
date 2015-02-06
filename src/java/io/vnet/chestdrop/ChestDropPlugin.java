package io.vnet.chestdrop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Matthew on 03/02/2015.
 */
public class ChestDropPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 4) {
            try {
                World world = Bukkit.getWorld(args[0]);
                if (world == null) {
                    sender.sendMessage("Couldn't find world with name " + args[0]);
                } else {
                    int x = Integer.parseInt(args[1]);
                    int y = Integer.parseInt(args[2]);
                    int z = Integer.parseInt(args[3]);
                    Location location = new Location(world, x, y, z);

                    if (location.getBlock().getType() == Material.AIR) {
                        FallingBlock block = world.spawnFallingBlock(location, Material.CHEST, (byte) 0);
                        block.setMetadata("droppedChest", new FixedMetadataValue(this, true));
                        sender.sendMessage("Spawned falling chest at x=" + x + " y=" + y + " z=" + z);
                    } else {
                        sender.sendMessage("Can't spawn chest inside another block");
                    }
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage("Invalid number provided");
            }
        } else {
            return false;
        }

        return true;
    }

    @EventHandler
    public void onBlockForm(EntityChangeBlockEvent event) {
        Entity ent = event.getEntity();

        if (ent.hasMetadata("droppedChest")) {
            final Block block = event.getBlock();

            // Run a tick later to check if the block actually formed
            getServer().getScheduler().runTask(this, new Runnable() {
                @Override
                public void run() {
                    if (block.getType() == Material.CHEST) {
                        Chest chest = (Chest) block.getState();

                        // Do whatever you want with the chest's inventory here
                        chest.getInventory().addItem(new ItemStack(Material.DIAMOND));
                    }
                }
            });
        }
    }
}
