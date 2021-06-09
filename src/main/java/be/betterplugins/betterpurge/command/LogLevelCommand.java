package be.betterplugins.betterpurge.command;

import be.betterplugins.betterpurge.messenger.BPLogger;
import be.betterplugins.betterpurge.messenger.Messenger;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class LogLevelCommand extends BPCommand
{

    private final BPLogger logger;

    public LogLevelCommand(Messenger messenger, BPLogger logger)
    {
        super(messenger);
        this.logger = logger;
    }

    @Override
    public String getName()
    {
        return "loglevel";
    }

    @Override
    public String getPermission()
    {
        return "betterpurge.loglevel";
    }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] arguments)
    {
        if (!player.isOp())
        {
            messenger.sendMessage(player, "&cFor security reasons, only OPs can execute this command");
            return true;
        }

        if (arguments.length < 2)
        {
            messenger.sendMessage(player, "You are using logging level: " + logger.getLogLevel());
            messenger.sendMessage(player, "&4To change the level, please use the command as: /bp loglevel <level>");
            return true;
        }

        Level level;
        try
        {
            level = Level.parse(arguments[1].toUpperCase());
        }
        catch (IllegalArgumentException | NullPointerException ignored)
        {
            messenger.sendMessage(player, "&4Please provide a valid level");
            messenger.sendMessage(player, "&cUse ALL to display all logs, FINE to get the beta testing logs, or INFO to run in normal mode");
            return true;
        }

        messenger.sendMessage(player, "Now using logging level: " + level.toString());
        logger.setLogLevel( level );

        return true;
    }
}
