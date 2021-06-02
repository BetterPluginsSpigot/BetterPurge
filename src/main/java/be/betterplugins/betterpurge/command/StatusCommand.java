package be.betterplugins.betterpurge.command;

import be.betterplugins.betterpurge.messenger.Messenger;
import be.betterplugins.betterpurge.messenger.MsgEntry;
import be.betterplugins.betterpurge.model.PurgeStatus;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class StatusCommand extends BPCommand
{

    private final PurgeStatus purgeStatus;

    public StatusCommand(Messenger messenger, PurgeStatus status)
    {
        super(messenger);

        this.purgeStatus = status;
    }

    @Override
    public String getName()
    {
        return "status";
    }

    @Override
    public String getPermission()
    {
        return "betterpurge.status";
    }

    @Override
    public boolean execute(@NotNull Player commandSender, @NotNull Command command, @NotNull String[] arguments)
    {
        DayOfWeek day = LocalDateTime.now().getDayOfWeek();
        messenger.sendMessage(
                commandSender,
                "status_purge_today",
                new MsgEntry("<var>", purgeStatus.getPurgeConfiguration().isDayEnabled( day ), "yes", "no")
        );
        messenger.sendMessage(
                commandSender,
                "status_purge_start",
                new MsgEntry("<time>",
                purgeStatus.getNextStartTime().toString())
        );
        messenger.sendMessage(
                commandSender,
                "status_purge_end",
                new MsgEntry("<time>",
                purgeStatus.getNextStartTime().addMinutes( purgeStatus.getPurgeConfiguration().getDuration() ).toString())
        );
        return true;
    }


}
