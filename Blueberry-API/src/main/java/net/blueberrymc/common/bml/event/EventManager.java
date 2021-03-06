package net.blueberrymc.common.bml.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import net.blueberrymc.common.Blueberry;
import net.blueberrymc.common.bml.BlueberryMod;
import net.blueberrymc.common.bml.loading.ModLoadingError;
import net.blueberrymc.common.bml.loading.ModLoadingErrors;
import net.blueberrymc.common.util.ThrowableConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ConcurrentHashMap<Class<? extends Event>, HandlerList> handlerMap = new ConcurrentHashMap<>();

    private void logInvalidHandler(Method method, String message, BlueberryMod mod) {
        LOGGER.warn("Invalid EventHandler: {} at {} in mod {}", message, method.toGenericString(), mod.getModId());
        ModLoadingErrors.add(new ModLoadingError(mod, String.format("Invalid EventHandler: %s at %s in mod %s", message, method.toGenericString(), mod.getModId()), true));
    }

    public void registerEvents(@NotNull BlueberryMod mod, @NotNull Listener listener) {
        Preconditions.checkNotNull(mod, "mod cannot be null");
        Preconditions.checkNotNull(listener, "listener cannot be null");
        for (Method method : listener.getClass().getMethods()) {
            if (method.isSynthetic()) continue;
            if (!method.isAnnotationPresent(EventHandler.class)) continue;
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            if (method.getParameterCount() != 1) {
                logInvalidHandler(method, "parameter count is not 1", mod);
                continue;
            }
            if (!method.getReturnType().equals(void.class)) {
                logInvalidHandler(method, "Warning: return type/value is not used", mod);
            }
            /* // TODO: do we need this check?
            if (Modifier.isAbstract(method.getModifiers())) {
                logInvalidHandler(method, "method must not be abstract");
                continue;
            }
            */
            Class<?> clazz = method.getParameters()[0].getType();
            if (!Event.class.isAssignableFrom(clazz)) {
                logInvalidHandler(method, "parameter type is not assignable from " + Event.class.getCanonicalName(), mod);
                continue;
            }
            Class<? extends Event> eventClass = clazz.asSubclass(Event.class);
            HandlerList handlerList = getHandlerList(eventClass);
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            ThrowableConsumer<Event> consumer = isStatic ? event -> method.invoke(null, event) : event -> method.invoke(listener, event);
            handlerList.add(consumer, eventHandler.priority(), listener, mod);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void registerEvent(@NotNull Class<T> clazz, @NotNull BlueberryMod mod, @NotNull EventPriority priority, @NotNull ThrowableConsumer<T> consumer) {
        getHandlerList(clazz).add(event -> consumer.accept((T) event), priority, null, mod);
    }

    public void unregisterEvents(@NotNull BlueberryMod mod) {
        Preconditions.checkNotNull(mod, "mod cannot be null");
        handlerMap.values().forEach(handlerList -> handlerList.remove(mod));
    }

    public void unregisterEvents(@NotNull Listener listener) {
        Preconditions.checkNotNull(listener, "listener cannot be null");
        handlerMap.values().forEach(handlerList -> handlerList.remove(listener));
    }

    /**
     * Calls an event.
     * @param event the event
     * @throws IllegalStateException Thrown when an asynchronous/synchronous event is fired from wrong thread.
     * @return the fired event
     */
    @Contract("_ -> param1")
    @NotNull
    public <T extends Event> T callEvent(@NotNull T event) {
        Preconditions.checkNotNull(event, "event cannot be null");
        if (Blueberry.getUtil().isOnGameThread() && event.isAsynchronous()) {
            throw new IllegalStateException(event.getEventName() + " cannot be triggered asynchronously from " + Thread.currentThread().getName());
        }
        if (!Blueberry.getUtil().isOnGameThread() && !event.isAsynchronous()) {
            throw new IllegalStateException(event.getEventName() + " cannot be triggered synchronously from " + Thread.currentThread().getName());
        }
        getHandlerList(event.getClass()).fire(event);
        return event;
    }

    @NotNull
    public Set<Class<? extends Event>> getKnownEvents() {
        return handlerMap.keySet();
    }

    @Contract(pure = true)
    @NotNull
    public static Map<Class<? extends Event>, HandlerList> getHandlerMap() {
        return ImmutableMap.copyOf(handlerMap);
    }

    @NotNull
    public static HandlerList getHandlerList(@NotNull Class<? extends Event> event) {
        Preconditions.checkNotNull(event, "event cannot be null");
        if (handlerMap.containsKey(event)) return handlerMap.get(event);
        try {
            Method method = event.getMethod("getHandlerList");
            if (!method.getReturnType().equals(HandlerList.class)) throw throwNoHandlerListError(event);
            if (!Modifier.isStatic(method.getModifiers())) throw new IllegalArgumentException("getHandlerList method on " + event.getCanonicalName() + " is not static method");
            HandlerList handlerList = (HandlerList) method.invoke(null);
            if (handlerList == null) throw new IllegalArgumentException("getHandlerList method on " + event.getCanonicalName() + " returned null");
            handlerMap.put(event, handlerList);
            return handlerList;
        } catch (NoSuchMethodException ex) {
            throw throwNoHandlerListError(event);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("getHandlerList method on " + event.getCanonicalName() + " is not accessible (make sure your method has 'public' modifier");
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("getHandlerList method on " + event.getCanonicalName() + " threw exception", e);
        }
    }

    private static RuntimeException throwNoHandlerListError(Class<? extends Event> event) {
        return new IllegalArgumentException(event.getCanonicalName() + " does not implement a static getHandlerList() method that does not have any parameters and returns HandlerList as a result");
    }
}
