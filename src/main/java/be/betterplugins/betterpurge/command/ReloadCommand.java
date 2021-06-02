package be.betterplugins.betterpurge.command;

import be.betterplugins.betterpurge.BetterPurge;
import be.betterplugins.betterpurge.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends BPCommand
{

    private final BetterPurge purgePlugin;

    public ReloadCommand(Messenger messenger, BetterPurge purgePlugin)
    {
        super(messenger);

        this.purgePlugin = purgePlugin;
    }

    @Override
    public String getName()
    {
        return "reload";
    }

    @Override
    public String getPermission()
    {
        return "betterpurge.reload";
    }

    @Override
    public boolean execute(@NotNull Player commandSender, @NotNull Command command, @NotNull String[] arguments)
    {
        purgePlugin.reload();
        messenger.sendMessage(commandSender, "reload_complete");
        return true;
    }
}
