package pandorum.listeners.events;

import mindustry.gen.Groups;
import pandorum.features.Effects;

public class OnTriggerUpdate implements Runnable {

    public void run() {
        Groups.player.each(player -> player.unit().moving(), player -> Effects.onMove(player.x, player.y));
    }
}
