package be.betterplugins.betterpurge;

import be.betterplugins.betterpurge.command.*;
import be.betterplugins.betterpurge.messenger.BPLogger;
import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.messenger.MsgEntry;
import be.betterplugins.betterpurge.model.PurgeHandler;
import be.betterplugins.betterpurge.model.PurgeStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CommandHandler implements CommandExecutor
{

    private final Messenger messenger;
    private final BPLogger logger;

    private final Map<String, BPCommand> commandMap;
    private final HelpCommand helpCommand;

    public CommandHandler(Messenger messenger, BPLogger logger, PurgeHandler purgeHandler, BetterPurge purgePlugin)
    {
        this.messenger = messenger;
        this.logger = logger;

        // When implementing unit testing, use a factory provided through the constructor to create BPCommand instances
        BPCommand status = new StatusCommand(messenger, purgeHandler.getPurgeStatus());
        BPCommand reload = new ReloadCommand(messenger, purgePlugin);
        BPCommand start = new StartCommand(messenger, purgeHandler);
        BPCommand stop = new StopCommand(messenger, purgeHandler);
        BPCommand logLevel = new LogLevelCommand(messenger, logger);

        this.commandMap = new HashMap<String, BPCommand>()
        {{
            put("status", status);
            put("s", status);

            put("reload", reload);
            put("r", reload);

            put("start", start);

            put("stop", stop);

            put("loglevel", logLevel);
        }};

        this.helpCommand = new HelpCommand(messenger, commandMap);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments)
    {

        logger.log(Level.FINEST, "Handling command: " + (arguments.length > 0 ? arguments[0] : "None provided"));

        // Only allow user commands
        if ( !(commandSender instanceof Player))
        {
            logger.log(Level.FINE, "Not a Player or ConsoleCommandSender. Cancelling command handling");

            // No support for command blocks
            messenger.sendMessage(commandSender, "&cOnly players can execute BetterPurge commands!");
            return true;
        }

        Player player = (Player) commandSender;

        // Default to /bs help if no argument given
        String cmd = (arguments.length == 0) ? "help" : arguments[0].toLowerCase();

        if (commandMap.containsKey( cmd ))
        {
            logger.log(Level.FINEST, "Found command: /bm " + cmd);

            BPCommand bmCommand = commandMap.get(cmd);
            if (commandSender.hasPermission( bmCommand.getPermission() ))
                return bmCommand.execute(player, command, arguments);
            else
                messenger.sendMessage(commandSender, "no_permission", new MsgEntry("<command>", "/bp " + cmd));
        }
        else
        {
            return this.helpCommand.execute( player, command, arguments );
        }

        return true;
    }
}
