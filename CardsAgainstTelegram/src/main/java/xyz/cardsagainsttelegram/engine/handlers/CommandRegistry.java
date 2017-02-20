package xyz.cardsagainsttelegram.engine.handlers;


import xyz.cardsagainsttelegram.bean.command.Command;

import java.util.HashMap;

public class CommandRegistry {
    private static HashMap<String, Command> commands = new HashMap<>();


    public static void registerCommand(Command cmd) {
        commands.put(cmd.getName().toLowerCase(), cmd);
    }

    public static Command getCommand(String cmd) {
        return commands.get(cmd.toLowerCase());
    }

    public static HashMap<String, Command> getCommands() {
        return commands;
    }

}
