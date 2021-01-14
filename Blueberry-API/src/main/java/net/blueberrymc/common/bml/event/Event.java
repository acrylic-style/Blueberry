package net.blueberrymc.common.bml.event;

import net.blueberrymc.common.Blueberry;
import net.blueberrymc.common.bml.BlueberryMod;
import net.blueberrymc.common.bml.EventManager;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event.
 * All events require a static method named getHandlerList() which returns the {@link HandlerList}.
 * @see EventManager#callEvent(Event)
 * @see EventManager#registerEvents(BlueberryMod,Listener)
 */
public abstract class Event {
    private String name;
    private final boolean async;

    protected Event() {
        this(false);
    }

    protected Event(boolean async) {
        this.async = async;
    }

    /**
     * Calls the event and tests if cancelled.
     *
     * @return false if event was cancelled, if cancellable. otherwise true.
     */
    public boolean callEvent() {
        Blueberry.getEventManager().callEvent(this);
        if (this instanceof Cancellable) {
            return !((Cancellable) this).isCancelled();
        } else {
            return true;
        }
    }

    public final boolean isAsynchronous() {
        return async;
    }

    @NotNull
    public final String getEventName() {
        if (name == null) {
            name = getClass().getSimpleName();
        }
        return name;
    }
}
