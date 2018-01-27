package pl.sebcel.minecraft.spawnmonsters;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnMonstersPlugin extends JavaPlugin {

    private Class<?>[] spawnerMonsterTypes = new Class<?>[] { Zombie.class, Skeleton.class, Creeper.class, Witch.class };

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("spawnmonsters")) {
            return false;
        }

        Player player = getPlayer(sender, args);
        if (player != null) {
            spawnMonsters(player);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns Player around which monsters should spawn
     * 
     * If executed from server console, player name must be provided as a command argument. If executed by a player, player is command executor by default
     */
    private Player getPlayer(CommandSender sender, String[] args) {
        // Player name provided as a command argument
        if (args.length == 1) {
            String playerName = args[0];
            Player player = getServer().getPlayer(playerName);
            if (player != null) {
                return player;
            } else {
                sender.sendMessage("Player " + playerName + " not found");
                return null;
            }
        }

        // Player name not provided as a command argument
        if (sender instanceof Player) {
            return (Player) sender;
        } else {
            sender.sendMessage("You need to provide player name when executed from the server console");
            return null;
        }
    }

    /**
     * Spawns three sets of montsters: 4 in distance of 5 meters, 16 in distance of 10 meters, and 64 in distance of 20 meters
     */
    private void spawnMonsters(Player player) {
        Bukkit.broadcastMessage("A horde of monsters will now appear near " + player.getName());
        spawnMonsters(player, 5, 4);
        spawnMonsters(player, 10, 16);
        spawnMonsters(player, 20, 64);
    }

    /**
     * Spawns random monster in a circular area around the player
     */
    private void spawnMonsters(Player player, double radius, double numberOfMonsters) {
        Location playerLocation = player.getLocation();
        double angleStep = Math.PI * 2 / numberOfMonsters;
        for (double angle = 0; angle < Math.PI * 2; angle += angleStep) {
            double deltaX = radius * Math.cos(angle);
            double deltaZ = radius * Math.sin(angle);
            Location monsterLocation = playerLocation.clone().add(deltaX, 0, deltaZ);
            if (canSpawnAt(monsterLocation)) {
                Class monsterClass = spawnerMonsterTypes[(int) (Math.random() * spawnerMonsterTypes.length)];
                player.getWorld().spawn(monsterLocation, monsterClass);
            }
        }
    }

    /**
     * Checks if a monster can be spawned in a given location
     */
    private boolean canSpawnAt(Location location) {
        return location.getBlock().isEmpty() && location.clone().add(0, 1, 0).getBlock().isEmpty();
    }
}