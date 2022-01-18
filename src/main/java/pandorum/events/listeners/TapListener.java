package pandorum.events.listeners;

import mindustry.game.EventType.TapEvent;
import pandorum.comp.Bundle;
import pandorum.entry.HistoryEntry;
import pandorum.struct.CacheSeq;

import static pandorum.Misc.findLocale;
import static pandorum.PluginVars.*;

public class TapListener {

    public static void call(final TapEvent event) {
        if (config.historyEnabled() && activeHistoryPlayers.contains(event.player.uuid()) && event.tile != null) {
            CacheSeq<HistoryEntry> entries = history[event.tile.x][event.tile.y];
            entries.cleanUp();
            StringBuilder history = new StringBuilder(Bundle.format("history.title", findLocale(event.player.locale), event.tile.x, event.tile.y));

            if (entries.isOverflown()) {
                history.append(Bundle.format("history.overflown", findLocale(event.player.locale)));
            }

            for (HistoryEntry entry : entries) {
                history.append("\n").append(entry.getMessage(event.player));
            }

            if (entries.isEmpty()) {
                history.append(Bundle.format("history.empty", findLocale(event.player.locale)));
            }

            event.player.sendMessage(history.toString());
        }
    }
}