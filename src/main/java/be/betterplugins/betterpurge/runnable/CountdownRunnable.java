package be.betterplugins.betterpurge.runnable;

import org.bukkit.scheduler.BukkitRunnable;

public class CountdownRunnable extends BukkitRunnable {

    private int count;

    private final ICountAction countAction;
    private final ICountAction doneAction;

    public CountdownRunnable(int startingCount, ICountAction countAction, ICountAction doneAction)
    {
        this.count = startingCount;

        this.countAction = countAction;
        this.doneAction = doneAction;
    }

    @Override
    public void run()
    {
        if (count > 0)
        {
            this.countAction.execute( count );
        }
        else
        {
            this.doneAction.execute( count );
            this.cancel();
        }

        count--;
    }
}
