package pandorum.commands.server;

import arc.Core;
import arc.func.Cons;
import arc.util.Log;
import arc.util.Structs;
import mindustry.game.Gamemode;
import mindustry.maps.Map;
import mindustry.maps.MapException;

import static mindustry.Vars.*;
import static pandorum.util.Search.findMap;

public class HostCommand implements Cons<String[]> {
    public void get(String[] args) {
        if (!state.isMenu()) {
            Log.err("Сервер уже запущен.");
            return;
        }

        Gamemode mode = args.length > 1 ? Structs.find(Gamemode.all, m -> m.name().equalsIgnoreCase(args[1])) : Gamemode.survival;
        if (mode == null) {
            Log.err("Режим игры '@' не найден.", args[1]);
            return;
        }

        Map map;
        if (args.length > 0) {
            map = findMap(args[0]);
            if (map == null) {
                Log.err("Карта '@' не найдена.", args[0]);
                return;
            }
        } else {
            map = maps.getShuffleMode().next(mode, state.map);
            Log.info("Случайным образом выбрана карта: '@'.", map.name());
        }

        logic.reset();

        Core.app.post(() -> {
            try {
                Log.info("Загружаю карту...");

                world.loadMap(map, map.applyRules(mode));
                state.rules = map.applyRules(mode);
                logic.play();

                Log.info("Карта загружена.");

                netServer.openServer();
            } catch (MapException e) {
                Log.err("@: @", e.map.name(), e.getMessage());
            }
        });
    }
}
