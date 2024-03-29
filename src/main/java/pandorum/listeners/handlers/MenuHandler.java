package pandorum.listeners.handlers;

import arc.Events;
import mindustry.game.EventType.GameOverEvent;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Unitc;
import mindustry.ui.Menus;
import pandorum.components.Bundle;
import pandorum.data.PlayerData;
import pandorum.features.Ranks.Rank;
import pandorum.util.Utils;

import static mindustry.Vars.state;
import static pandorum.data.Database.getPlayerData;
import static pandorum.data.Database.setPlayerData;
import static pandorum.util.PlayerUtils.bundled;
import static pandorum.util.PlayerUtils.sendToChat;
import static pandorum.util.Search.findLocale;

public class MenuHandler {

    public static int welcomeMenu, despawnMenu, artvMenu, statsMenu, rankInfoMenu, ranksRequirementsMenu, rankIncreaseMenu;

    public static void load() {
        welcomeMenu = Menus.registerMenu((player, option) -> {
            if (option == 1) {
                PlayerData data = getPlayerData(player.uuid());
                data.welcomeMessage = false;
                setPlayerData(player.uuid(), data);
                bundled(player, "welcome.disabled");
            }
        });

        despawnMenu = Menus.registerMenu((player, option) -> {
            if (!player.admin) return;

            switch (option) {
                case 0 -> {
                    Groups.unit.each(Unitc::kill);
                    bundled(player, "commands.despawn.success.all");
                }
                case 2 -> {
                    Groups.unit.each(Unitc::isPlayer, Unitc::kill);
                    bundled(player, "commands.despawn.success.players");
                }
                case 3 -> {
                    Groups.unit.each(unit -> unit.team == state.rules.defaultTeam, Unitc::kill);
                    bundled(player, "commands.despawn.success.team", Utils.coloredTeam(state.rules.defaultTeam));
                }
                case 4 -> {
                    Groups.unit.each(unit -> unit.team == state.rules.waveTeam, Unitc::kill);
                    bundled(player, "commands.despawn.success.team", Utils.coloredTeam(state.rules.waveTeam));
                }
                case 5 -> {
                    Call.unitCapDeath(player.unit());
                    bundled(player, "commands.despawn.success.suicide");
                }
            }
        });

        artvMenu = Menus.registerMenu((player, option) -> {
            if (!player.admin) return;

            if (option == 0) {
                Events.fire(new GameOverEvent(state.rules.waveTeam));
                sendToChat("commands.artv.info", player.name);
            }
        });

        statsMenu = emptyMenu();

        rankInfoMenu = Menus.registerMenu((player, option) -> {
            if (option == 1) {
                StringBuilder builder = new StringBuilder();
                Rank.ranks.each(rank -> rank.req != null, rank -> builder.append(Bundle.format("commands.rank.menu.requirements.content", findLocale(player.locale), rank.tag, rank.displayName, rank.req.playTime() / 60, rank.req.buildingsBuilt(), rank.req.gamesPlayed())).append("\n"));

                Call.menu(player.con, ranksRequirementsMenu,
                        Bundle.format("commands.rank.menu.requirements.header", findLocale(player.locale)),
                        builder.toString(),
                        new String[][] {{Bundle.format("ui.menus.close", findLocale(player.locale))}}
                );
            }
        });

        ranksRequirementsMenu = emptyMenu();

        rankIncreaseMenu = emptyMenu();
    }

    public static int emptyMenu() {
        return Menus.registerMenu((player, option) -> {});
    }
}
