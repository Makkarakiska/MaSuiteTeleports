package dev.masa.masuiteteleports.bungee;

import dev.masa.masuitecore.bungee.chat.Formator;
import dev.masa.masuitecore.core.configuration.BungeeConfiguration;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Button {

    private BungeeConfiguration config = new BungeeConfiguration();
    private Formator formator = new Formator();

    private String type;
    private String command;

    public Button(String type, String command) {
        this.type = type;
        this.command = command;
    }

    public TextComponent create() {
        TextComponent btn = new TextComponent();
        btn.addExtra(formator.colorize(config.load("teleports", "buttons.yml").getString("buttons." + this.type + ".text")));
        btn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(formator.colorize(
                config.load("teleports", "buttons.yml").getString("buttons." + this.type + ".hover"))).create()
        ));
        btn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, this.command));
        return btn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
