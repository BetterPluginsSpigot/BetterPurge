package be.betterplugins.betterpurge.messenger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Messenger {


    private final Map<String, String> messages;     // Contains the messages in lang.yml by mapping path to value
    private final boolean doShortenPrefix;          // Whether or not the short prefix variant is to be used

    private final String shortPrefix = "&9[BP] &3";
    private final String longPrefix = "&9[BetterPurge] &3";

    private final BPLogger logger;

    /**
     * Creates a messenger for player output
     * @param messages the messages from lang.yml, mapping path to message
     * @param doShortenPrefix whether to use the short prefix (true) or the long prefix (false)
     */
    public Messenger(Map<String, String> messages, BPLogger logger, boolean doShortenPrefix)
    {
        this.messages = messages;
        this.logger = logger;
        this.doShortenPrefix = doShortenPrefix;
    }


    /**
     * Compose a ready-to-be-sent BetterSleeping message
     * @param messageID the ID of the message, or a custom message
     * @param replacements the tag replacements for this message
     * @return the message ready to be sent
     */
    public String composeMessage(String messageID, MsgEntry... replacements)
    {
        return this.composeMessage(messageID, true, replacements);
    }

    /**
     * Compose a ready-to-be-sent BetterSleeping message
     * @param messageID the ID of the message, or a custom message
     * @param includePrefix whether or not a prefix should be put in front of this message
     * @param replacements the tag replacements for this message
     * @return the message ready to be sent
     */
    public String composeMessage(String messageID, boolean includePrefix, MsgEntry... replacements)
    {
        // Get the message from lang.yml OR if non existent, get the raw message
        String message = messages.getOrDefault(messageID, messageID);

        // Early return if the message is disabled
        if (message.equals("") || message.equalsIgnoreCase("ignored"))
            return "";

        if (message.equals( messageID ))
        {
            logger.log(Level.INFO, "Missing language option found: " + messageID + ". Consider adding it to the language file");
        }

        // Perform variable replacements
        for (MsgEntry entry : replacements)
        {
            message = message.replace(entry.getTag(), entry.getReplacement());
        }

        // Singular/plural support
        String[] replaceThis = StringUtils.substringsBetween(message, "[", "]");
        if (replaceThis != null)
        {
            String[] replaceBy = new String[replaceThis.length];
            for (int i = 0; i < replaceThis.length; i++)
            {
                String[] options = replaceThis[i].split("\\.");
                if (options.length >= 3)
                {
                    try
                    {
                        double amount = Double.parseDouble(options[0]);
                        replaceBy[i] = amount == 1 ? options[1] : options[2];
                    }
                    catch(NumberFormatException exception)
                    {
                        replaceBy[i] = options[1];
                    }
                }
                else if (options.length >= 1)
                {
                    replaceBy[i] = options[options.length - 1];
                }
            }

            message = StringUtils.replaceEach(message, replaceThis, replaceBy);
            message = message.replaceAll("\\[", "").replaceAll("]", "");
        }

        // Get the prefix and put it before the message
        if (includePrefix)
        {
            String prefix = doShortenPrefix ? shortPrefix : longPrefix;
            message = prefix + message;
        }

        // Perform final replacements for color
        message = message.replace('&', '§');

        // Perform final replacement to allow square brackets []
        message = message.replaceAll("\\|\\(", "[");
        message = message.replaceAll("\\)\\|", "]");

        return message;
    }


    /**
     * Send a message from lang.yml to a CommandSender
     * If the message does not exist, it will be sent to the player in its raw form
     * As optional parameter, a list or several MsgEntries can be given as parameter
     * @param receiver the receiver
     * @param messageID the id of the message
     * @param replacements The strings that are to be replaced to allow using variables in messages
     * @return False if this message is disabled (set to "" or "ignored"), true otherwise
     */
    public boolean sendMessage(CommandSender receiver, String messageID, MsgEntry... replacements)
    {
        return sendMessage(Collections.singletonList(receiver), messageID, replacements);
    }



    /**
     * Send a message from lang.yml to a list of players
     * If the message does not exist, it will be sent to the player in its raw form
     * As optional parameter, a list or several MsgEntries can be given as parameter
     * @param receivers the list of players
     * @param messageID the id of the message
     * @param replacements The strings that are to be replaced to allow using variables in messages
     * @return False if this message is disabled (set to "" or "ignored"), true otherwise
     */
    public boolean sendMessage(List<? extends CommandSender> receivers, String messageID, MsgEntry... replacements)
    {
        // Compose the message and return if message is disabled
        String message = composeMessage(messageID, replacements);
        if (message.equals(""))
            return false;

        // Get the player if there is a player who did an action
        Player placeholderPlayer = null;
        for (MsgEntry entry : replacements)
            if (entry.getTag().equals("<player>"))
                placeholderPlayer = Bukkit.getPlayer(entry.getReplacement());


        // Send everyone a message
        for (CommandSender receiver : receivers)
        {
            // Get the senders name
            String name = receiver.getName();
            String finalMessage = message.replace("<user>", ChatColor.stripColor( name ));
            sendMessage(receiver, finalMessage);
        }

        return true;
    }

    protected void sendMessage(CommandSender receiver, String message)
    {
        receiver.sendMessage( message );
    }
}