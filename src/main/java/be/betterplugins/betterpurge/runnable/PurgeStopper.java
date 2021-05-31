package be.betterplugins.betterpurge.runnable;

import be.betterplugins.betterpurge.listener.ContainerListener;
import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.model.PurgeState;
import be.betterplugins.betterpurge.model.PurgeStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PurgeStopper extends BukkitRunnable
{
    private final PurgeStatus status;
    private final Messenger messenger;
    private final ContainerListener containerListener;

    public PurgeStopper(PurgeStatus status, ContainerListener containerListener, Messenger messenger)
    {
        this.status = status;
        this.messenger = messenger;
        this.containerListener = containerListener;
    }

    @Override
    public void run()
    {
        if (status.getState() == PurgeState.ACTIVE)
        {
            this.containerListener.closeAll();
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            messenger.sendMessage(players, "purge_disabled");
            status.setState(PurgeState.DISABLED);
        }
    }
}
