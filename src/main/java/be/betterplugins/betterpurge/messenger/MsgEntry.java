package be.betterplugins.betterpurge.messenger;

public class MsgEntry {

    private final String tag;
    private final String replacement;

    /**
     * Keeps track of a tag and its replacement for use in PlayerMessenger
     * @param tag the original tag
     * @param replacement the replacement value of this tag
     */
    public MsgEntry(String tag, String replacement)
    {
        this.tag = tag;
        this.replacement = replacement;
    }

    /**
     * Keeps track of a tag and its replacement for use in PlayerMessenger
     * @param tag the original tag
     * @param replacement the replacement value of this tag
     */
    public MsgEntry(String tag, int replacement)
    {
        this(tag, "" + replacement);
    }

    /**
     * Keeps track of a tag and its replacement for use in PlayerMessenger
     * @param tag the original tag
     * @param replacement the replacement value of this tag
     */
    public MsgEntry(String tag, double replacement)
    {
        this(tag, "" + replacement);
    }

    /**
     * Keeps track of a tag and its replacement for use in PlayerMessenger
     * @param tag the original tag
     * @param replacement the replacement value of this tag
     */
    public MsgEntry(String tag, boolean replacement)
    {
        this(tag, replacement, "true", "false");
    }

    /**
     * Keeps track of a tag and its replacement for use in PlayerMessenger
     * @param tag the original tag
     * @param replacement the replacement value of this tag
     */
    public MsgEntry(String tag, boolean replacement, String trueValue, String falseValue)
    {
        this(tag, replacement ? trueValue : falseValue);
    }

    /**
     * Get the tag that should be replaced
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Gets the value by which the tag should be replaced
     * @return the replacement
     */
    public String getReplacement() {
        return replacement;
    }
}