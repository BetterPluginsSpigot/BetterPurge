package be.betterplugins.betterpurge.listener;

import be.betterplugins.betterpurge.collections.DoubleMap;
import be.betterplugins.betterpurge.messenger.BPLogger;
import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.model.InventorySync;
import be.betterplugins.betterpurge.model.PurgeState;
import be.betterplugins.betterpurge.model.PurgeStatus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

public class ContainerListener implements Listener {

    private final PurgeStatus purgeStatus;
    private final Messenger messenger;
    private final BPLogger logger;

    private final Set<InventoryType> allowedContainers;

    private final DoubleMap<UUID, Location, InventorySync> inventoryMap;

    public ContainerListener(PurgeStatus purgeStatus, Messenger messenger, BPLogger logger)
    {
        this.purgeStatus = purgeStatus;
        this.messenger = messenger;
        this.logger = logger;

        this.inventoryMap = new DoubleMap<>();

        allowedContainers = new HashSet<InventoryType>()
        {{
            add(InventoryType.CHEST);
            add(InventoryType.BARREL);
            add(InventoryType.BREWING);
            add(InventoryType.DISPENSER);
            add(InventoryType.DROPPER);
            add(InventoryType.SHULKER_BOX);
        }};
    }

    /**
     * Call when the purge should stop: Close all inventories and reset the state
     */
    public void closeAll()
    {
        for (UUID uuid : this.inventoryMap.keySetForward())
        {
            Player p = Bukkit.getPlayer( uuid );
            if (p != null)
                p.closeInventory();
        }

        this.inventoryMap.clear();
    }

    /**
     * Highest priority to fire as late as possible (but not monitor because we may alter the event outcome ourselves)
      */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestOpen(InventoryOpenEvent event)
    {

        Inventory inventory = event.getInventory();
        Location location = inventory.getLocation();
        HumanEntity player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Prevent an infinite loop and later make sure this player will not end up in an infinite loop of calling this event
        if (this.inventoryMap.containsForward( uuid ))
            return;

        this.logger.log(Level.FINEST, "Player '" + player.getName() + " opened inventory type: '" + inventory.getType().toString() + "'");

        // Only consider allowed containers
        if (!allowedContainers.contains( inventory.getType() ))
        {
            this.logger.log(Level.FINEST, "Player '" + player.getName() + "' opened an inventory that is not considered by BetterPurge");
            return;
        }

        // Don't do anything if the purge is not active
        if (purgeStatus.getState() != PurgeState.ACTIVE)
        {
            this.logger.log(Level.FINER, "The purge is not active, not handling this event");
            return;
        }

        // Don't allow opening this container if it is already opened by someone else
        if (this.inventoryMap.containsBackward( location ) || inventory.getViewers().size() > 1)
        {
            event.setCancelled(true);
            logger.log(Level.FINER, "Player '" + player.getName() + "' tried to open a container that was already open");
            messenger.sendMessage(player, "chest_already_open");
            return;
        }

        logger.log(Level.FINER, "Player '" + player.getName() + "' opened an inventory, which is now locked for others");

        // Open the 'dummy' inventory because opening the original may be blocked by another plugin
        InventorySync invSync = new InventorySync(player, inventory);

        // Mark the player as having opened an inventory
        this.inventoryMap.put(uuid, location, invSync);

        // Open the copied inventory
        player.openInventory( invSync.getCopy() );
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onChestClose(InventoryCloseEvent event)
    {
        HumanEntity player = event.getPlayer();

        InventoryType type = event.getInventory().getType();
        if (this.allowedContainers.contains( type ))
            this.logger.log(Level.FINEST, "Player '" + player.getName() + " closed inventory type: '" + type.toString() + "'");
        else return;

        // Mark player as not having opened an inventory and remove the inventory from being active
        InventorySync invSync = this.inventoryMap.removeForward( player.getUniqueId() );
        if (invSync != null)
        {
            invSync.syncToOriginal();
        }
        else
        {
            this.logger.log(Level.FINE, "An inventory closed that was not found. Cannot update contents");
        }
    }

}
