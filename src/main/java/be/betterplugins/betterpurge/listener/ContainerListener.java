package be.betterplugins.betterpurge.listener;

import be.betterplugins.betterpurge.collections.DoubleMap;
import be.betterplugins.betterpurge.model.InventorySync;
import be.betterplugins.betterpurge.model.PurgeStatus;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ContainerListener implements Listener {

    private final PurgeStatus purgeStatus;
    private final Set<InventoryType> allowedContainers;
    private final Set<UUID> activePlayers;
    private final DoubleMap<Inventory, Inventory, InventorySync> openInventories;

    public ContainerListener(PurgeStatus purgeStatus)
    {
        this.purgeStatus = purgeStatus;
        this.activePlayers = new HashSet<>();
        this.openInventories = new DoubleMap<>();
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChestOpen(InventoryOpenEvent event)
    {
//        // Don't do anything if the purge is not active
//        if (purgeStatus.getState() != PurgeState.ACTIVE)
//            return;

        HumanEntity player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Inventory inventory = event.getInventory();

        // Only consider allowed containers
        if (!allowedContainers.contains( inventory.getType() ))
            return;

        // Prevent an infinite loop
        if (this.activePlayers.contains( uuid ))
            return;

        // Make sure this player will not end up in an infinite loop of calling this event
        this.activePlayers.add( uuid );

        // Open the 'dummy' inventory if someone else is busy in this inventory
        // This syncs the inventory automatically
        InventorySync invSync;
        if (openInventories.containsForward( inventory ))
        {
            player.sendMessage("VIEWING OPENED INV");

            invSync = openInventories.getForward( inventory );
        }
        else
        {
            // TODO: Remove all viewers
            player.sendMessage("OPEN NEW INV");

            invSync = new InventorySync(player, inventory);
            this.openInventories.put( inventory, invSync.getCopy(), invSync );

        }

        // Add a viewer and open the copied inventory
        invSync.addViewer();
        player.openInventory( invSync.getCopy() );
    }


    @EventHandler
    public void onChestClose(InventoryCloseEvent event)
    {
        // Mark player as not having opened an inventory
        this.activePlayers.remove( event.getPlayer().getUniqueId() );

        Inventory inv = event.getInventory();
        InventorySync invSync;

        // Get the relevant InventorySync object, or return
        if(this.openInventories.containsBackward( inv ))
            invSync = this.openInventories.getBackward( inv );
        else if (this.openInventories.containsForward( inv ))
            invSync = this.openInventories.getForward( inv );
        else return;

        invSync.removeViewer();
        if (!invSync.hasViewers())
        {
            event.getPlayer().sendMessage("Remove fro mmap");
            invSync.syncToOriginal();

            if (this.openInventories.containsBackward( inv ))
                this.openInventories.removeBackward( inv );
            else
                this.openInventories.removeForward( inv );
        }
    }

}
