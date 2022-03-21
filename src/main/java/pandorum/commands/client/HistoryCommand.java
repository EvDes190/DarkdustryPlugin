package pandorum.commands.client;

import arc.util.CommandHandler.CommandRunner;
import mindustry.gen.Player;

import static pandorum.PluginVars.activeHistoryPlayers;
import static pandorum.util.Utils.bundled;

public class HistoryCommand implements CommandRunner<Player> {
    public void accept(String[] args, Player player) {
        if (activeHistoryPlayers.remove(player.uuid())) {
            bundled(player, "commands.history.off");
            return;
        }

        activeHistoryPlayers.add(player.uuid());
        bundled(player, "commands.history.on");
    }
}
