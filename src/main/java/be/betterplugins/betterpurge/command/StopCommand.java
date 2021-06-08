package be.betterplugins.betterpurge.command;

import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.model.PurgeHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StopCommand extends BPCommand
{

    private final PurgeHandler purgeHandler;

    public StopCommand(Messenger messenger, PurgeHandler purgeHandler)
    {
        super(messenger);
        this.purgeHandler = purgeHandler;
    }

    @Override
    public String getName()
    {
        return "stop";
    }

    @Override
    public String getPermission()
    {
        return "betterpurge.stop";
    }

    @Override
    public boolean execute(@NotNull Player commandSender, @NotNull Command command, @NotNull String[] arguments)
    {
        messenger.sendMessage( new ArrayList<>(Bukkit.getOnlinePlayers() ), "purge_force_stop");
        purgeHandler.stopPurge();
        return true;
    }
}
