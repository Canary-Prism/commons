package canaryprism.commons.event;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EventListener;
import java.util.Objects;
import java.util.Set;

/// Event Listener List mostly like how the Swing class behaves
public class EventListenerList<L extends EventListener> {
    
    private final HashSetValuedHashMap<Class<?>, Object> map = new HashSetValuedHashMap<>();
    
    /// Adds a listener to this listener list
    ///
    /// If the passed listener implements multiple types of listeners only the one that is the `type` passed will receive events
    ///
    /// If the passed listener is null or is already added as a listener of the passed type this method returns `false`
    /// otherwise returns `true`
    ///
    /// @param type the runtime class of the listener
    /// @param listener the listener to add
    /// @param <T> the type of the listener
    /// @return whether the listener is successfully added
    /// @throws IllegalArgumentException if the listener is not an instance of the type
    /// @throws NullPointerException if the passed type is null
    public <T extends L> boolean addListener(@NotNull Class<T> type, @Nullable T listener) {
        Objects.requireNonNull(type, "event listener type must not be null");
        
        if (listener == null)
            return false;
        
        if (!type.isInstance(listener))
            throw new IllegalArgumentException(String.format("%s is not of type %s", listener, type));
        
        return map.put(type, listener);
    }
    
    /// Removes a listener from this listener list
    ///
    /// If the passed listener is added as multiple types of listeners only the one that is the `type` passed will stop receiving events
    ///
    /// If the passed listener is null or is already not stored as a listener of the passed type this method returns `false`
    /// otherwise returns `true`
    ///
    /// @param type the runtime class of the listener
    /// @param listener the listener to remove
    /// @param <T> the type of the listener
    /// @return whether the listener is successfully removed
    /// @throws NullPointerException if the passed type is null
    public <T extends L> boolean removeListener(@NotNull Class<T> type, @Nullable T listener) {
        return map.removeMapping(Objects.requireNonNull(type, "event listener type must not be null"), listener);
    }
    
    /// Gets listeners registered as the provided type
    ///
    /// @param type the runtime class of the listeners to get
    /// @param <T> the type
    /// @return a set of listeners of the type
    /// @throws NullPointerException if the passed type is null
    @SuppressWarnings("unchecked")
    public <T extends L> Set<? extends T> getListeners(@NotNull Class<T> type) {
        return  ((Set<T>)((Set<?>) map.get(Objects.requireNonNull(type, "event listener type must not be null"))));
    }
}
