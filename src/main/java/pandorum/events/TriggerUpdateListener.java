package pandorum.events;

import mindustry.gen.Groups;
import pandorum.PandorumPlugin;
import pandorum.comp.effects.Effects;
import pandorum.comp.Ranks;

public class TriggerUpdateListener {
    public static void call() {
        Groups.player.each(p -> p.unit().moving(), Effects::onMove);
        if (PandorumPlugin.interval.get(1, 30f)) {
            Groups.player.each(player -> Ranks.getRank(player, rank -> player.name(rank.tag + "[#" + player.color.toString().toUpperCase() + "]" + player.getInfo().lastName)));
        }
    }
}
