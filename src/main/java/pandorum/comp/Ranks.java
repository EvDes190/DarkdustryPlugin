package pandorum.comp;

import arc.func.Cons;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.gen.Call;
import mindustry.gen.Player;
import pandorum.models.PlayerModel;

import static pandorum.Misc.findLocale;
import static pandorum.Misc.millisecondsToMinutes;

public class Ranks {

    public static final Rank admin = new Rank("[accent]<[scarlet]\uE817[accent]> ", "[scarlet]Admin", 4);
    public static final Rank veteran = new Rank("[accent]<[gold]\uE809[accent]> ", "[gold]Veteran", 3, new Requirements(1500 * 60 * 1000L, 100000, 100), null);
    public static final Rank activePlus = new Rank("[accent]<[white]\uE813[accent]> ", "[sky]Active+", 2, new Requirements(750 * 60 * 1000L, 50000, 30), veteran);
    public static final Rank active = new Rank("[accent]<[white]\uE800[accent]> ", "[cyan]Active", 1, new Requirements(250 * 60 * 1000L, 25000, 15), activePlus);
    public static final Rank player = new Rank("[accent]Player", 0, null, active);

    private static final Seq<Rank> ranks = Seq.with(player, active, activePlus, veteran, admin);

    public static Rank get(int index) {
        return ranks.get(index);
    }

    public static Rank getRank(Player player, int index) {
        return player.admin ? admin : get(index);
    }

    public static void updateRank(Player player, Cons<Rank> cons) {
        if (player.admin) {
            cons.get(admin);
            return;
        }

        PlayerModel.find(player.uuid(), playerModel -> {
            Rank current = get(playerModel.rank);

            if (current.next != null && current.next.req != null && current.next.req.check(playerModel.playTime, playerModel.buildingsBuilt, playerModel.gamesPlayed)) {
                Call.infoMessage(player.con, Bundle.format("events.rank-increase",
                        findLocale(player.locale),
                        current.next.tag,
                        current.next.name,
                        millisecondsToMinutes(playerModel.playTime),
                        playerModel.buildingsBuilt,
                        playerModel.gamesPlayed
                ));

                playerModel.rank = current.next.id;
                playerModel.save();
                cons.get(current.next);
                return;
            }

            cons.get(current);
        });
    }

    public static void updateName(Player player, Cons<String> cons) {
        updateRank(player, rank -> cons.get(Strings.format("@[#@]@", rank.tag, player.color.toString(), player.getInfo().lastName)));
    }

    public static void updateName(Player player) {
        updateName(player, player::name);
    }

    public static class Rank {
        public final String tag;
        public final String name;
        public final int id;
        public final Rank next;
        public final Requirements req;

        public Rank(String tag, String name, int id, Requirements req, Rank next) {
            this.name = name;
            this.tag = tag;
            this.id = id;
            this.req = req;
            this.next = next;
        }

        public Rank(String name, int id, Requirements req, Rank next) {
            this("", name, id, req, next);
        }

        public Rank(String tag, String name, int id) {
            this(tag, name, id, null, null);
        }
    }

    public static class Requirements {
        public final long playtime;
        public final int buildingsBuilt;
        public final int gamesPlayed;

        public Requirements(long playtime, int buildingsBuilt, int gamesPlayed) {
            this.playtime = playtime;
            this.buildingsBuilt = buildingsBuilt;
            this.gamesPlayed = gamesPlayed;
        }

        public boolean check(long time, int built, int games) {
            return time >= playtime && built >= buildingsBuilt && games >= gamesPlayed;
        }
    }
}
