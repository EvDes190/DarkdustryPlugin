package pandorum.commands.client;

import arc.math.Mathf;
import arc.util.CommandHandler.CommandRunner;
import arc.util.Strings;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.gen.Unit;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;
import static pandorum.util.PlayerUtils.bundled;

public class TeleportCommand implements CommandRunner<Player> {
    public void accept(String[] args, Player player) {
        if (!player.admin) {
            bundled(player, "commands.permission-denied");
            return;
        }

        if (!Strings.canParsePositiveInt(args[0]) || !Strings.canParsePositiveInt(args[1])) {
            bundled(player, "commands.tp.incorrect-number-format");
            return;
        }

        int x = Mathf.clamp(Strings.parseInt(args[0]), 0, world.width()), y = Mathf.clamp(Strings.parseInt(args[1]), 0, world.height());
        boolean spawnedNyCore = player.unit().spawnedByCore();
        Unit unit = player.unit();

        unit.spawnedByCore(false);
        player.clearUnit();

        unit.set(x * tilesize, y * tilesize);
        Call.setPosition(player.con, x * tilesize, y * tilesize);
        Call.setCameraPosition(player.con, x * tilesize, y * tilesize);

        player.unit(unit);
        unit.spawnedByCore(spawnedNyCore);
    }
}
