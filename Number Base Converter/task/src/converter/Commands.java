package converter;

public enum Commands {

    back(-1, "/back"),
    exit(-2, "/exit");

    final int commandCode;
    final String commandName;

    Commands(int code, String name) {
        this.commandCode = code;
        this.commandName = name;
    }
}
