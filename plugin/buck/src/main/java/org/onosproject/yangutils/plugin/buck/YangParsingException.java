package org.onosproject.yangutils.plugin.buck;

/**
 * Used to signal a problem parsing a Yang model.
 */
public class YangParsingException extends Exception {

    /**
     * Creates a YangParsingException based on another exception.
     * @param t exception from the parser
     */
    public YangParsingException (Throwable t) {
        super(t);
    }

}
