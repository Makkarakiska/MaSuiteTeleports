package fi.matiaspaavilainen.masuiteteleports;

import fi.matiaspaavilainen.masuitecore.chat.Formator;
import fi.matiaspaavilainen.masuitecore.config.Configuration;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Button {

    private Configuration config = new Configuration();
    private Formator formator = new Formator();

    public Button() {
    }

    public TextComponent create(String type, String command) {
        TextComponent btn = new TextComponent();
        btn.addExtra(formator.colorize(config.load("teleports", "buttons.yml").getString("buttons." + type + ".text")));
        btn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(formator.colorize(
                        config.load("teleports", "buttons.yml").getString("buttons." + type + ".hover"))
                ).create()
        ));
        btn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return btn;
    }
}
