package be.betterplugins.betterpurge.model;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

public class InventorySync
{
    private final Inventory original;
    private final Inventory copy;

    public InventorySync(HumanEntity player, Inventory original)
    {
        this.original = original;
        int originalSize = original.getSize();
        int size = originalSize% 9 == 0 ? originalSize : originalSize + (9 - (originalSize % 9));
        this.copy = Bukkit.createInventory(player, size, "Purging inventory");
        this.copy.setContents( this.original.getContents() );
    }

    public void syncToOriginal()
    {
        for (int slot = 0; slot < copy.getSize(); slot++) {
            this.original.setItem(slot, this.copy.getItem(slot));
        }
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
