package pandorum.discord;

import arc.util.CommandHandler;
import arc.util.CommandHandler.CommandResponse;
import arc.util.CommandHandler.ResponseType;
import arc.util.Log;
import arc.util.Strings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;

import static pandorum.PluginVars.config;

public class Bot {
    public static final CommandHandler discordHandler = new CommandHandler(config.discordBotPrefix);

    public static JDA jda;

    public static Guild guild;
    public static Role adminRole;
    public static MessageChannel botChannel, adminChannel;

    public static void init() {
        try {
            jda = JDABuilder.createDefault(config.discordBotToken).addEventListeners(new BotListener()).build();
            Log.info("[Darkdustry] Бот успешно запущен...");
        } catch (Exception e) {
            Log.err("[Darkdustry] Ошибка запуска бота...");
            Log.err(e);
        }
    }
    public static void handleMessage(Message message) {
        CommandResponse response = discordHandler.handleMessage(message.getContentRaw(), message);
        if (response.type == ResponseType.fewArguments || response.type == ResponseType.manyArguments) {
            err(message.getChannel(), "Неверное количество аргументов.", "Использование : **@@** @", discordHandler.getPrefix(), response.command.text, response.command.paramText);
        }
    }

    public static boolean adminCheck(Member member) {
        return member.getRoles().contains(adminRole);
    }

    /**
     * Различные методы для отправки сообщений и эмбедов.
     */

    public static void updateBotStatus() {

    }

    public static void text(MessageChannel channel, String text, Object... args) {
        channel.sendMessage(Strings.format(text, args)).queue();
    }

    public static void text(String text, Object... args) {
        text(botChannel, text, args);
    }

    public static void info(MessageChannel channel, String title, String text, Object... args) {
        channel.sendMessageEmbeds(new EmbedBuilder().addField(title, Strings.format(text, args), true).setColor(Color.orange).build()).queue();
    }

    public static void err(MessageChannel channel, String title, String text, Object... args) {
        channel.sendMessageEmbeds(new EmbedBuilder().addField(title, Strings.format(text, args), true).setColor(Color.red).build()).queue();
    }
}
