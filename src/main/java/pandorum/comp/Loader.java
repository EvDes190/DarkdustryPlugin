package pandorum.comp;

import arc.Events;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.Log;
import arc.util.io.Streams;
import mindustry.game.EventType;
import mindustry.net.Administration;
import pandorum.PandorumPlugin;
import pandorum.admin.Authme;
import pandorum.discord.BotMain;
import pandorum.effects.Effects;
import pandorum.events.*;
import pandorum.ranks.Ranks;

import java.io.InputStream;
import java.util.Objects;

import static mindustry.Vars.netServer;

public class Loader {
    public static void init() {
        try {
            InputStream stream = Loader.class.getClassLoader().getResourceAsStream("vpn-ipv4.txt");
            Objects.requireNonNull(stream, "stream");
            PandorumPlugin.forbiddenIps = Seq.with(Streams.copyString(stream).split(System.lineSeparator())).map(IpInfo::new);
        } catch(Exception e) {
            throw new ArcRuntimeException(e);
        }

        Administration.Config.showConnectMessages.set(false);
        Administration.Config.strict.set(true);
        Administration.Config.motd.set("off");
        Administration.Config.messageRateLimit.set(1);
        Administration.Config.enableVotekick.set(true);

        netServer.admins.addActionFilter(ActionFilter::filter);
        netServer.admins.addChatFilter(ChatFilter::filter);

        Events.on(EventType.PlayerUnbanEvent.class, PlayerUnbanListener::call);
        Events.on(EventType.PlayerBanEvent.class, PlayerBanListener::call);
        Events.on(EventType.ServerLoadEvent.class, ServerLoadListener::call);
        Events.on(EventType.WorldLoadEvent.class, WorldLoadListener::call);
        Events.on(EventType.BlockBuildEndEvent.class, BlockBuildEndListener::call);
        Events.on(EventType.ConfigEvent.class, ConfigListener::call);
        Events.on(EventType.TapEvent.class, TapListener::call);
        Events.on(EventType.DepositEvent.class, DepositListener::call);
        Events.on(EventType.WithdrawEvent.class, WithdrawListener::call);
        Events.on(EventType.BuildSelectEvent.class, BuildSelectListener::call);
        Events.on(EventType.PlayerJoin.class, PlayerJoinListener::call);
        Events.on(EventType.PlayerLeave.class, PlayerLeaveListener::call);
        Events.on(EventType.GameOverEvent.class, GameOverListener::call);
        Events.on(EventType.WaveEvent.class, WaveEventListener::call);
        Events.run(EventType.Trigger.update, TriggerUpdateListener::call);

        Effects.init();
        Ranks.init();
        MenuListener.init();
        Icons.init();
        Authme.init();
        PandorumPlugin.socket.connect();

        BotMain.run();

        Log.info("[Darkdustry]: Сервер запущен и готов к работе!");
    }
}
