package be.betterplugins.betterpurge.command;

import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.model.PurgeHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StartCommand extends BPCommand
{

    private final PurgeHandler purgeHandler;

    public StartCommand(Messenger messenger, PurgeHandler purgeHandler)
    {
        super(messenger);
        this.purgeHandler = purgeHandler;
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
        int duration = 0;
        if (arguments.length >= 2)
        {
            try
            {
                duration = Integer.parseInt(arguments[1]);
            }
            catch (NumberFormatException ignored)
            {
                messenger.sendMessage(commandSender, "not_a_number");
                return true;
            }
        }
        messenger.sendMessage( new ArrayList<>(Bukkit.getOnlinePlayers() ), "purge_force_start");
        if (duration >= 1)
        {
            this.purgeHandler.startPurge( duration );
        }
        else
        {
            this.purgeHandler.startPurge();
        }
        return true;
    }
}
