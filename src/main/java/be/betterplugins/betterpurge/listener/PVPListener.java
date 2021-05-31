package be.betterplugins.betterpurge.listener;

import be.betterplugins.betterpurge.messenger.BPLogger;
import be.betterplugins.betterpurge.model.PurgeConfiguration;
import be.betterplugins.betterpurge.model.PurgeState;
import be.betterplugins.betterpurge.model.PurgeStatus;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.logging.Level;

public class PVPListener implements Listener
{

    private final PurgeStatus purgeStatus;
    private final PurgeConfiguration purgeConfig;
    private final BPLogger logger;

    public PVPListener(PurgeStatus purgeStatus, PurgeConfiguration purgeConfig, BPLogger logger)
    {
        this.purgeStatus = purgeStatus;
        this.purgeConfig = purgeConfig;
        this.logger = logger;
    }

    /**
     * Fire as late as possible to be able to overwrite the outcome (but not MONITOR, because we may cancel the event)
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAttack(EntityDamageByEntityEvent event)
    {
        Entity attackingEntity = event.getDamager();
        Entity attackedEntitiy = event.getEntity();

        // Ignore any attacks where one or more of the parties is not a player
        if (attackedEntitiy.getType() != EntityType.PLAYER || attackingEntity.getType() != EntityType.PLAYER)
        {
            this.logger.log(Level.FINEST, "Attack event: either the attacking or attacked entity is not a player. Ignoring...");
            return;
        }

        // Only handle PVP when enabled
        if (!purgeConfig.shouldHandlePVP())
            return;

        // Don't allow pvp outside of the purge
        if (purgeStatus.getState() != PurgeState.ACTIVE)
        {
            this.logger.log(Level.FINER, "Disallowing PVP when the purge is not active!");
            event.setCancelled( true );
        }
        // Overwrite PVP if it is not allowed here (when enabled)
        else if ( purgeConfig.shouldOverwriteSafezonePvp() && event.isCancelled() && attackedEntitiy instanceof Damageable)
        {
            this.logger.log(Level.FINER, "Overwriting disabled PVP...");
            Damageable damageable = (Damageable) attackedEntitiy;
            damageable.damage( event.getFinalDamage() );
        }
        // PVP is allowed by default: no action required
        else
        {
            this.logger.log(Level.FINEST, "No need to handle PVP, it is allowed by default");
        }
    }

}
