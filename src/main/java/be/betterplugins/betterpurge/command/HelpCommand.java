package be.betterplugins.betterpurge.command;

import be.betterplugins.betterpurge.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HelpCommand extends BPCommand
{

    private final Set<BPCommand> commandSet;

    public HelpCommand(Messenger messenger, Map<String, BPCommand> commandMap)
    {
        super(messenger);
        this.commandSet = new HashSet<>(commandMap.values());
    }

    @Override
    public String getName()
    {
        return "help";
    }

    @Override
    public String getPermission()
    {
        return "betterpurge.help";
    }

    @Override
    public boolean execute(@NotNull Player commandSender, @NotNull Command command, @NotNull String[] arguments)
    {
        messenger.sendMessage(commandSender, "help_intro");
        for (BPCommand cmd : commandSet)
            if (commandSender.hasPermission( cmd.getPermission() ))
                messenger.sendMessage(commandSender, "help_" + cmd.getName());
        return true;
    }
}
