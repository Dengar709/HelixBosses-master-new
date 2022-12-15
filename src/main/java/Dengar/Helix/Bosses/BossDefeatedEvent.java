package Dengar.Helix.Bosses;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BossDefeatedEvent extends Event {
    private final Boss boss;

    public BossDefeatedEvent(Boss boss) {
        this.boss = boss;
    }

    @NotNull
    public Boss getBoss() {
        return boss;
    }

    // EVENT IMPL (Its all magic from here on out)

    private static final HandlerList HANDLERS = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}