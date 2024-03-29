package pandorum.commands.client;

import arc.util.CommandHandler.CommandRunner;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.world.Block;
import pandorum.components.Icons;
import pandorum.util.Utils;

import static pandorum.PluginVars.teamsList;
import static pandorum.util.PlayerUtils.bundled;
import static pandorum.util.Search.findTeam;

public class CoreCommand implements CommandRunner<Player> {
    public void accept(String[] args, Player player) {
        if (!player.admin) {
            bundled(player, "commands.permission-denied");
            return;
        }

        Block core = args.length > 0 ? switch (args[0].toLowerCase()) {
            case "shard" -> Blocks.coreShard;
            case "foundation" -> Blocks.coreFoundation;
            case "nucleus" -> Blocks.coreNucleus;
            case "bastion" -> Blocks.coreBastion;
            case "citadel" -> Blocks.coreCitadel;
            case "acropolis" -> Blocks.coreAcropolis;
            default -> null;
        } : Blocks.coreShard;

        if (core == null) {
            bundled(player, "commands.core.core-not-found");
            return;
        }

        Team team = args.length > 1 ? findTeam(args[1]) : player.team();
        if (team == null) {
            bundled(player, "commands.team-not-found", teamsList);
            return;
        }

        Call.constructFinish(player.tileOn(), core, player.unit(), (byte) 0, team, false);
        bundled(player, player.tileOn() != null && player.tileOn().block() == core ? "commands.core.success" : "commands.core.failed", Icons.get(core.name), Utils.coloredTeam(team));
    }
}
