package pandorum.commands.client;

import arc.util.CommandHandler.CommandRunner;
import arc.util.Strings;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.type.UnitType;
import pandorum.components.Icons;
import pandorum.util.Utils;

import static pandorum.PluginVars.*;
import static pandorum.util.PlayerUtils.bundled;
import static pandorum.util.Search.findTeam;
import static pandorum.util.Search.findUnit;

public class SpawnCommand implements CommandRunner<Player> {
    public void accept(String[] args, Player player) {
        if (!player.admin) {
            bundled(player, "commands.permission-denied");
            return;
        }

        if (args.length > 1 && !Strings.canParsePositiveInt(args[1])) {
            bundled(player, "commands.not-int");
            return;
        }

        UnitType type = findUnit(args[0]);
        if (type == null || type == UnitTypes.block) {
            bundled(player, "commands.unit-not-found", unitsList);
            return;
        }

        int amount = args.length > 1 ? Strings.parseInt(args[1]) : 1;
        if (amount < 1 || amount > maxSpawnAmount) {
            bundled(player, "commands.spawn.limit", maxSpawnAmount);
            return;
        }

        Team team = args.length > 2 ? findTeam(args[2]) : player.team();
        if (team == null) {
            bundled(player, "commands.team-not-found", teamsList);
            return;
        }

        for (int i = 0; i < amount; i++) type.spawn(team, player.x, player.y);
        bundled(player, "commands.spawn.success", amount, Icons.get(type.name), Utils.coloredTeam(team));
    }
}
