package pandorum.commands.client;

import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.CommandHandler.CommandRunner;
import arc.util.Strings;
import mindustry.gen.Player;
import mindustry.maps.Map;
import pandorum.components.Bundle;

import static mindustry.Vars.maps;
import static mindustry.Vars.state;
import static pandorum.util.PlayerUtils.bundled;
import static pandorum.util.Search.findLocale;

public class MapsListCommand implements CommandRunner<Player> {
    public void accept(String[] args, Player player) {
        if (args.length > 0 && !Strings.canParseInt(args[0])) {
            bundled(player, "commands.page-not-int");
            return;
        }

        Seq<Map> mapsList = maps.customMaps();
        int page = args.length > 0 ? Strings.parseInt(args[0]) : 1;
        int pages = Mathf.ceil(mapsList.size / 8f);

        if (--page >= pages || page < 0) {
            bundled(player, "commands.under-page", pages);
            return;
        }

        StringBuilder result = new StringBuilder(Bundle.format("commands.maps.page", findLocale(player.locale), page + 1, pages));

        for (int i = 8 * page; i < Math.min(8 * (page + 1), mapsList.size); i++) {
            Map map = mapsList.get(i);
            result.append("\n[lightgray] ").append(i).append(". [orange]").append(map.name());
        }

        result.append(Bundle.format("commands.maps.current", findLocale(player.locale), state.map.name()));

        player.sendMessage(result.toString());
    }
}
