package xyz.cardsagainsttelegram.engine.handlers;


import lombok.Getter;
import xyz.cardsagainsttelegram.bean.command.Command;

import java.util.HashMap;

public class CommandRegistry {
    @Getter
    private static HashMap<String, Command> commands = new HashMap<>();


    public static void registerCommand(Command cmd) {
        commands.put(cmd.getName().toLowerCase(), cmd);
    }

    public static Command getCommand(String cmd) {
        return commands.get(cmd.toLowerCase());
    }

}
