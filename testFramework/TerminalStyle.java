package testFramework;

public enum TerminalStyle {
    RED(31),
    GREEN(32),
    MAGENTA(35),
    CYAN(36),
    RESET(39);

    private final static boolean isPrettyTerminalEnabled = true;

    private final String ansiCommand;

    TerminalStyle(int code) {
        this.ansiCommand = "\u001b[" + code + "m";
    }

    public String toString() {
        return isPrettyTerminalEnabled ? ansiCommand : "";
    }

    public String format(String format, Object... args) {
        return format(String.format(
            // We don't know if strings inserted into format may have color info, so
            // re-apply our color after every inserted string
            format.replace("%s", "%s" + this),
            args
        ));
    }

    public String format(Object obj) {
        return this + obj.toString() + RESET;
    }
}
