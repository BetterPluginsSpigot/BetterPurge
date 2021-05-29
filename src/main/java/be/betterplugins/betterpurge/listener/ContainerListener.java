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

//        // No action required if no plugin stops this event
//        if (!event.isCancelled())
//            return;

        // Open the 'dummy' inventory if someone else is busy in this inventory
        // This syncs the inventory automatically
        if (openInventories.containsForward( inventory ))
        {
            player.sendMessage("OPEN existing INV");
            this.activePlayers.add( uuid );
            Inventory copy = openInventories.getForward( inventory ).getCopy();
            event.getPlayer().openInventory( copy );
        }
        else
        {

            // Someone breaks into this container: Close all viewers of the original chest to prevent duplication issues
//        for (HumanEntity viewer : inventory.getViewers())
//        {
//            player.sendMessage("FORCE CLOSE");
//            viewer.closeInventory();
//        }

            player.sendMessage("OPEN NEW INV");

            InventorySync invSync = new InventorySync(player, inventory);
            this.openInventories.put( inventory, invSync.getCopy(), invSync );
            this.activePlayers.add( uuid );
            player.openInventory( invSync.getCopy() );
        }
    }

//    @EventHandler
//    public void onItemInteract(InventoryClickEvent event)
//    {
//        event.getWhoClicked().sendMessage("CLICK");
//        UUID uuid = event.getWhoClicked().getUniqueId();
//        if ( activePlayers.containsKey( uuid ))
//            activePlayers.get( uuid ).syncToOriginal();
//    }
//
//    @EventHandler
//    public void onItemDrag(InventoryDragEvent event)
//    {
//        event.getWhoClicked().sendMessage("DRAG");
//        UUID uuid = event.getWhoClicked().getUniqueId();
//        if ( activePlayers.containsKey( uuid ))
//            activePlayers.get( uuid ).syncToOriginal();
//    }

    @EventHandler
    public void onChestClose(InventoryCloseEvent event)
    {
        this.activePlayers.remove( event.getPlayer().getUniqueId() );

        event.getPlayer().sendMessage("Close event");

        Inventory inv = event.getInventory();
        // No one is watching anymore, remove from the map + update the original inventory
        if (this.openInventories.containsBackward( inv ) && inv.getViewers().size() < 2 )
        {
            event.getPlayer().sendMessage("Remove fro mmap");
            this.openInventories.removeBackward( inv ).syncToOriginal();
        }
    }

}
