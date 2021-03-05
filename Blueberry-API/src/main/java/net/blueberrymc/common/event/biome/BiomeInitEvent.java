package net.blueberrymc.common.event.biome;

import com.google.common.base.Preconditions;
import net.blueberrymc.common.bml.event.Event;
import net.blueberrymc.common.bml.event.HandlerList;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the biome is being created from the biome builder.
 */
public class BiomeInitEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    private final Biome.BiomeBuilder builder;
    private Biome biome;

    public BiomeInitEvent(@NotNull Biome.BiomeBuilder builder, @NotNull Biome biome) {
        this.builder = builder;
        this.biome = biome;
    }

    /**
     * Gets the biome builder for the event.
     * @return the builder
     */
    @NotNull
    public Biome.BiomeBuilder getBuilder() {
        return builder;
    }

    /**
     * Gets the biome being created.
     * @return the biome
     */
    @NotNull
    public Biome getBiome() {
        return biome;
    }

    public void setBiome(@NotNull Biome biome) {
        Preconditions.checkNotNull(biome, "biome cannot be null");
        this.biome = biome;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
