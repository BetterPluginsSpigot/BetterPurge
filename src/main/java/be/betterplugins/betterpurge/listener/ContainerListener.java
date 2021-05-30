package be.betterplugins.betterpurge.listener;

import be.betterplugins.betterpurge.messenger.BPLogger;
import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.model.InventorySync;
import be.betterplugins.betterpurge.model.PurgeState;
import be.betterplugins.betterpurge.model.PurgeStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.logging.Level;

public class ContainerListener implements Listener {

    private final PurgeStatus purgeStatus;
    private final Messenger messenger;
    private final BPLogger logger;

    private final Set<InventoryType> allowedContainers;

    private final Set<UUID> activePlayers;
    private final Map<Inventory, InventorySync> activeInventories;

    public ContainerListener(PurgeStatus purgeStatus, Messenger messenger, BPLogger logger)
    {
        this.purgeStatus = purgeStatus;
        this.messenger = messenger;
        this.logger = logger;

        this.activePlayers = new HashSet<>();
        this.activeInventories = new HashMap<>();

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
        for (UUID uuid : this.activePlayers)
        {
            Player p = Bukkit.getPlayer( uuid );
            if (p != null)
                p.closeInventory();
        }

        this.activePlayers.clear();
        this.activeInventories.clear();
    }

    /**
     * Highest priority to fire as late as possible (but not monitor because we may alter the event outcome ourselves)
      */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestOpen(InventoryOpenEvent event)
    {

        Inventory inventory = event.getInventory();
        HumanEntity player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Prevent an infinite loop and later make sure this player will not end up in an infinite loop of calling this event
        if (this.activePlayers.contains( uuid ))
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

        System.out.println(inventory.getLocation());

        // Don't allow opening this container if it is already opened by someone else
        if (activeInventories.containsKey( inventory ) || inventory.getViewers().size() > 1)
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
        this.activePlayers.add( uuid );
        this.activeInventories.put( inventory, invSync );

        // Open the copied inventory
        player.openInventory( invSync.getCopy() );
    }


    @EventHandler
    public void onChestClose(InventoryCloseEvent event)
    {
        HumanEntity player = event.getPlayer();

        this.logger.log(Level.FINEST, "Player '" + player.getName() + " closed inventory type: '" + event.getInventory().getType().toString() + "'");

        // Mark player as not having opened an inventory and remove the inventory from being active
        this.activePlayers.remove( player.getUniqueId() );
        InventorySync invSync = this.activeInventories.remove( event.getInventory() );
        if (invSync != null)
        {
            invSync.syncToOriginal();
        }
    }

}
