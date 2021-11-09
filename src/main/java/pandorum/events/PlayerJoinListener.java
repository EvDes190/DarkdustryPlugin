package pandorum.events;

import arc.util.Log;
import arc.util.Strings;
import com.mongodb.BasicDBObject;
import discord4j.core.spec.EmbedCreateSpec;
import mindustry.game.EventType;
import mindustry.gen.Call;
import pandorum.PandorumPlugin;
import pandorum.comp.Bundle;
import pandorum.comp.Effects;
import pandorum.comp.Ranks;
import pandorum.discord.BotHandler;
import pandorum.discord.BotMain;
import pandorum.models.PlayerModel;

import static pandorum.Misc.*;

public class PlayerJoinListener {
    public static void call(final EventType.PlayerJoin event) {
        Ranks.getRank(event.player, rank -> event.player.name(rank.tag + "[#" + event.player.color.toString().toUpperCase() + "]" + event.player.getInfo().lastName));

        Log.info("@ зашёл на сервер, IP: @, ID: @", event.player.getInfo().lastName, event.player.ip(), event.player.uuid());
        sendToChat("events.player-join", event.player.color.toString().toUpperCase(), event.player.getInfo().lastName);

        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(BotMain.successColor)
                .title(Strings.format("@ зашел на сервер.", Strings.stripColors(event.player.getInfo().lastName)))
                .build();

        BotHandler.sendEmbed(embed);

        Effects.onJoin(event.player);

        boolean[] vpn = {false};
        PandorumPlugin.antiVPN.checkIp(event.player.ip(), result -> vpn[0] = result);
        if (vpn[0]) {
            event.player.con.kick(Bundle.format("events.vpn-ip", findLocale(event.player.locale)));
            return;
        }

        PlayerModel.find(new BasicDBObject("UUID", event.player.uuid()), playerInfo -> {
            if (playerInfo.hellomsg) {
                String[][] options = {{Bundle.format("events.hellomsg.ok", findLocale(event.player.locale))}, {Bundle.format("events.hellomsg.disable", findLocale(event.player.locale))}};
                Call.menu(event.player.con, MenuListener.welcomeMenu, Bundle.format("events.hellomsg.header", findLocale(event.player.locale)), Bundle.format("events.hellomsg", findLocale(event.player.locale), PandorumPlugin.discordServerLink), options);
            }
        });
        
        bundled(event.player, "events.motd");
    }
}
