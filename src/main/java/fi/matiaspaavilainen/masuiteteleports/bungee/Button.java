package fi.matiaspaavilainen.masuiteteleports.bungee;

import fi.matiaspaavilainen.masuitecore.bungee.chat.Formator;
import fi.matiaspaavilainen.masuitecore.core.configuration.BungeeConfiguration;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Button {

    private BungeeConfiguration config = new BungeeConfiguration();
    private Formator formator = new Formator();

    public Button() {
    }

    public TextComponent create(String type, String command) {
        TextComponent btn = new TextComponent();
        btn.addExtra(formator.colorize(config.load("teleports", "buttons.yml").getString("buttons." + type + ".text")));
        btn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(formator.colorize(
                        config.load("teleports", "buttons.yml").getString("buttons." + type + ".hover"))).create()
        ));
        btn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return btn;
    }
}
