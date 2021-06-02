package be.betterplugins.betterpurge.command;

import be.betterplugins.betterpurge.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class BPCommand
{

    protected final Messenger messenger;

    public BPCommand(Messenger messenger)
    {
        this.messenger = messenger;
    }

    public abstract String getName();
    public abstract String getPermission();
    public abstract boolean execute(@NotNull Player commandSender, @NotNull Command command, @NotNull String[] arguments);

}