package pandorum.commands.client;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.CommandHandler.CommandRunner;
import arc.util.Strings;
import mindustry.gen.Groups;
import mindustry.gen.Iconc;
import mindustry.gen.Player;
import pandorum.components.Bundle;

import static pandorum.util.PlayerUtils.bundled;
import static pandorum.util.Search.findLocale;

public class PlayersListCommand implements CommandRunner<Player> {
    public void accept(String[] args, Player player) {
        if (args.length > 0 && !Strings.canParseInt(args[0])) {
            bundled(player, "commands.page-not-int");
            return;
        }

        Seq<Player> playersList = Groups.player.copy(new Seq<>());
        int page = args.length > 0 ? Strings.parseInt(args[0]) : 1;
        int pages = Mathf.ceil(playersList.size / 8f);

        if (--page >= pages || page < 0) {
            bundled(player, "commands.under-page", pages);
            return;
        }

        StringBuilder result = new StringBuilder(Bundle.format("commands.players.page", findLocale(player.locale), page + 1, pages));

        for (int i = 8 * page; i < Math.min(8 * (page + 1), playersList.size); i++) {
            result.append("\n[#9c88ee]* [white]");
            Player p = playersList.get(i);
            if (p.admin) result.append(Iconc.admin).append(" ");
            result.append(p.name).append(" [lightgray]([accent]ID: ").append(p.id).append("[lightgray])").append(" [lightgray]([accent]Locale: ").append(p.locale).append("[lightgray])");
        }

        player.sendMessage(result.toString());
    }
}
