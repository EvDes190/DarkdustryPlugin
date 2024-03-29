package pandorum.commands.client;

import arc.util.CommandHandler.CommandRunner;
import mindustry.gen.Call;
import mindustry.gen.Player;
import pandorum.components.Bundle;
import pandorum.data.PlayerData;
import pandorum.features.Ranks;
import pandorum.features.Ranks.Rank;

import static pandorum.data.Database.getPlayerData;
import static pandorum.listeners.handlers.MenuHandler.statsMenu;
import static pandorum.util.PlayerUtils.bundled;
import static pandorum.util.Search.findLocale;
import static pandorum.util.Search.findPlayer;

public class StatsCommand implements CommandRunner<Player> {
    public void accept(String[] args, Player player) {
        Player target = args.length > 0 ? findPlayer(args[0]) : player;
        if (target == null) {
            bundled(player, "commands.player-not-found", args[0]);
            return;
        }

        PlayerData data = getPlayerData(target.uuid());
        Rank rank = Ranks.getRank(data.rank);

        Call.menu(player.con, statsMenu,
                Bundle.format("commands.stats.menu.header", findLocale(player.locale), target.name),
                Bundle.format("commands.stats.menu.content", findLocale(player.locale), rank.tag, rank.displayName, data.playTime / 60, data.buildingsBuilt, data.gamesPlayed),
                new String[][] {{Bundle.format("ui.menus.close", findLocale(player.locale))}}
        );
    }
}
