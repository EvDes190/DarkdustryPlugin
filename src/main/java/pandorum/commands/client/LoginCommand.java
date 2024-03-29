package pandorum.commands.client;

import arc.util.CommandHandler.CommandRunner;
import arc.util.Timekeeper;
import mindustry.gen.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.awt.*;

import static pandorum.PluginVars.*;
import static pandorum.discord.Bot.adminChannel;
import static pandorum.features.Authme.*;
import static pandorum.util.PlayerUtils.bundled;

public class LoginCommand implements CommandRunner<Player> {
    public void accept(String[] args, Player player) {
        if (player.admin) {
            bundled(player, "commands.login.already-admin");
            return;
        }

        Timekeeper cooldown = loginCooldowns.get(player.uuid(), () -> new Timekeeper(loginCooldownTime));
        if (!cooldown.get()) {
            bundled(player, "commands.cooldown", loginCooldownTime / 60);
            return;
        }

        adminChannel.sendMessage(new MessageBuilder()
                .setEmbeds(new EmbedBuilder()
                        .setColor(Color.cyan)
                        .setTitle("Запрос на получение прав администратора.")
                        .addField("Никнейм:", player.name, true)
                        .addField("UUID:", player.uuid(), true)
                        .setFooter("Нажмите на кнопку, чтобы подтвердить или отклонить запрос. Подтверждайте только свои запросы!")
                        .build()
                ).setActionRows(ActionRow.of(confirm, deny, info)).build()
        ).queue(message -> loginWaiting.put(message, player.uuid()));

        bundled(player, "commands.login.sent");
        cooldown.reset();
    }
}
