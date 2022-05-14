package pandorum.listeners.handlers;

import arc.Events;
import mindustry.game.EventType.GameOverEvent;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Unitc;
import mindustry.ui.Menus;
import pandorum.components.Bundle;
import pandorum.features.Ranks.Rank;
import pandorum.mongo.models.MapModel;
import pandorum.mongo.models.PlayerModel;
import pandorum.util.Utils;

import static mindustry.Vars.state;
import static pandorum.PluginVars.canVote;
import static pandorum.PluginVars.mapRateVotes;
import static pandorum.util.Search.findLocale;

public class MenuHandler {

    public static int welcomeMenu, despawnMenu, artvMenu, mapRateMenu, statsMenu, rankInfoMenu, ranksRequirementsMenu, rankIncreaseMenu;

    public static void load() {
        welcomeMenu = Menus.registerMenu((player, option) -> {
            if (option == 1) {
                PlayerModel.find(player, playerModel -> {
                    playerModel.welcomeMessage = false;
                    playerModel.save();
                    Utils.bundled(player, "events.welcome.disabled");
                });
            }
        });

        despawnMenu = Menus.registerMenu((player, option) -> {
            switch (option) {
                case 0 -> {
                    Groups.unit.each(Unitc::kill);
                    Utils.bundled(player, "commands.admin.despawn.success.all");
                }
                case 2 -> {
                    Groups.unit.each(Unitc::isPlayer, Unitc::kill);
                    Utils.bundled(player, "commands.admin.despawn.success.players");
                }
                case 3 -> {
                    Groups.unit.each(unit -> unit.team == state.rules.defaultTeam, Unitc::kill);
                    Utils.bundled(player, "commands.admin.despawn.success.team", Utils.coloredTeam(state.rules.defaultTeam));
                }
                case 4 -> {
                    Groups.unit.each(unit -> unit.team == state.rules.waveTeam, Unitc::kill);
                    Utils.bundled(player, "commands.admin.despawn.success.team", Utils.coloredTeam(state.rules.waveTeam));
                }
                case 5 -> {
                    if (!player.dead()) player.unit().kill();
                    Utils.bundled(player, "commands.admin.despawn.success.suicide");
                }
            }
        });

        artvMenu = Menus.registerMenu((player, option) -> {
            if (option == 0) {
                Events.fire(new GameOverEvent(state.rules.waveTeam));
                Utils.sendToChat("commands.admin.artv.info", player.coloredName());
            }
        });

        mapRateMenu = Menus.registerMenu((player, option) -> {
            if ((option == 0 || option == 1)) {
                if (mapRateVotes.contains(player.uuid())) {
                    Utils.bundled(player, "commands.map.already-voted");
                    return;
                }

                if (!canVote) {
                    Utils.bundled(player, "commands.can-not-vote");
                    return;
                }

                MapModel.find(state.map, mapModel -> {
                    if (option == 0) {
                        mapModel.upVotes++;
                        mapModel.save();
                        mapRateVotes.add(player.uuid());
                        Utils.bundled(player, "commands.map.upvoted");
                    } else {
                        mapModel.downVotes++;
                        mapModel.save();
                        mapRateVotes.add(player.uuid());
                        Utils.bundled(player, "commands.map.downvoted");
                    }
                });
            }
        });

        statsMenu = emptyMenu();

        rankInfoMenu = Menus.registerMenu((player, option) -> {
            if (option == 1) {
                StringBuilder builder = new StringBuilder();
                Rank.ranks.each(rank -> rank.req != null, rank -> builder.append(Bundle.format("commands.rank.menu.requirements.content", findLocale(player.locale), rank.tag, rank.displayName, Utils.secondsToMinutes(rank.req.playTime), rank.req.buildingsBuilt, rank.req.gamesPlayed)).append("\n"));

                Call.menu(player.con,
                        ranksRequirementsMenu,
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
