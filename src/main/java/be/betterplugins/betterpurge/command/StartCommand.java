package be.betterplugins.betterpurge.command;

import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.model.PurgeStatus;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StartCommand extends BPCommand
{

    private final PurgeStatus purgeStatus;

    public StartCommand(Messenger messenger, PurgeStatus purgeStatus)
    {
        super(messenger);
        this.purgeStatus = purgeStatus;
    }

    @Override
    public String getName()
    {
        return "start";
    }

    @Override
    public String getPermission()
    {
        return "betterpurge.start";
    }

    @Override
    public boolean execute(@NotNull Player commandSender, @NotNull Command command, @NotNull String[] arguments)
    {
        this.purgeStatus.setStartNow();
        messenger.sendMessage( new ArrayList<>(Bukkit.getOnlinePlayers() ), "purge_force_start");
        return true;
    }
}
