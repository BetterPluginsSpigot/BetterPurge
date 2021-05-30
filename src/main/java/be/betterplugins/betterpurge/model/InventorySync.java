package be.betterplugins.betterpurge.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventorySync
{
    private final static String inventoryName = "Purging inventory";

    private final Inventory original;
    private final Inventory copy;

    public InventorySync(Inventory original)
    {
        this.original = original;

        if (original.getType() == InventoryType.CHEST)
        {
            int originalSize = original.getSize();
            this.copy = Bukkit.createInventory(null, originalSize, inventoryName);
        }
        else
        {
            this.copy = Bukkit.createInventory(null, original.getType(), inventoryName);
        }

        // Copy the inventory contents
        for (int i = 0; i < this.original.getSize(); i++)
            this.copy.setItem( i, this.original.getItem(i) );

    }

    public void syncToOriginal()
    {
        for (int i = 0; i < this.original.getSize(); i++)
            this.original.setItem( i, this.copy.getItem(i) );
    }

    public Location getLocation()
    {
        return original.getLocation();
    }

    public Inventory getOriginal()
    {
        return original;
    }

    public Inventory getCopy()
    {
        return copy;
    }
}
