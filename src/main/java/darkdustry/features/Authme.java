package darkdustry.features;

import arc.func.Func;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.awt.Color;

import static darkdustry.PluginVars.loginWaiting;
import static darkdustry.components.Bundle.bundled;
import static mindustry.Vars.netServer;

public class Authme {

    public static final SelectMenu menu = SelectMenu.create("authme")
            .addOption("Подтвердить", "authme.confirm", "Подтвердить запрос.",                  Emoji.fromFormatted("✅"))
            .addOption("Отклонить",   "authme.deny",    "Отклонить запрос.",                    Emoji.fromFormatted("❌"))
            .addOption("Информация",  "authme.info",    "Посмотреть всю информацию об игроке.", Emoji.fromFormatted("🔎"))
            .setPlaceholder("Выбери действие...").build();

    public static void confirm(GenericComponentInteractionCreateEvent event) {
        remove(event, player -> {
            netServer.admins.adminPlayer(player.uuid(), player.usid());
            player.admin(true);

            bundled(player, "commands.login.confirm");
            return new EmbedBuilder().setColor(Color.green).setTitle("Запрос подтвержден");
        });
    }

    public static void deny(GenericComponentInteractionCreateEvent event) {
        remove(event, player -> {
            bundled(player, "commands.login.deny");
            return new EmbedBuilder().setColor(Color.red).setTitle("Запрос отклонён");
        });
    }

    public static void info(GenericComponentInteractionCreateEvent event) {
        String uuid = loginWaiting.get(event.getMessage());
        var info = netServer.admins.getInfo(uuid);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.yellow)
                .setTitle("Информация об игроке")
                .addField("Никнейм:", info.lastName, true)
                .addField("UUID:", info.id, true)
                .addField("IP адрес:", info.lastIP, true)
                .addField("Зашел на сервер:", info.timesJoined + " раз", true)
                .addField("Выгнан с сервера:", info.timesKicked + " раз", true)
                .addField("Все никнеймы:", info.names.toString(), true)
                .addField("Все IP адреса", info.ips.toString(), true);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }

    private static void remove(GenericComponentInteractionCreateEvent event, Func<Player, EmbedBuilder> func) {
        String uuid = loginWaiting.remove(event.getMessage());
        var player = Groups.player.find(p -> p.uuid().equals(uuid));

        if (player != null) event.getChannel().sendMessageEmbeds(func.get(player)
                .addField("Администратор:", event.getUser().getAsMention(), true)
                .addField("Игрок:", player.name, true).build()).queue();

        event.getMessage().delete().queue();
    }
}