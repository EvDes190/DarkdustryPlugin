package pandorum.commands.client;

import arc.util.CommandHandler.CommandRunner;
import arc.util.Timekeeper;
import mindustry.gen.Player;
import mindustry.net.Administration.Config;
import pandorum.vote.VoteKickSession;

import static pandorum.PluginVars.*;
import static pandorum.util.PlayerUtils.bundled;
import static pandorum.util.Search.findPlayer;

public class VoteKickCommand implements CommandRunner<Player> {
    public void accept(String[] args, Player player) {
        if (!Config.enableVotekick.bool()) {
            bundled(player, "commands.votekick.disabled");
            return;
        }

        if (currentVoteKick != null) {
            bundled(player, "commands.vote-already-started");
            return;
        }

        Timekeeper cooldown = voteKickCooldowns.get(player.uuid(), () -> new Timekeeper(voteKickCooldownTime));
        if (!cooldown.get() && !player.admin) {
            bundled(player, "commands.cooldown", voteKickCooldownTime / 60);
            return;
        }

        Player target = findPlayer(args[0]);
        if (target == null) {
            bundled(player, "commands.player-not-found", args[0]);
            return;
        }

        if (target == player) {
            bundled(player, "commands.votekick.player-is-you");
            return;
        }

        if (target.admin) {
            bundled(player, "commands.votekick.player-is-admin");
            return;
        }

        if (target.team() != player.team()) {
            bundled(player, "commands.votekick.player-is-enemy");
            return;
        }

        currentVoteKick = new VoteKickSession(player, target);
        currentVoteKick.vote(player, 1);
        cooldown.reset();
    }
}
