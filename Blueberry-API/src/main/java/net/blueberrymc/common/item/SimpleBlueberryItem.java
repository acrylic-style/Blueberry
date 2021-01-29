package net.blueberrymc.common.item;

import net.blueberrymc.common.bml.BlueberryMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SimpleBlueberryItem extends BlueberryItem {
    private final Function<ItemStack, Component> toNameFunction;

    public SimpleBlueberryItem(@NotNull("mod") BlueberryMod mod, Properties properties, @NotNull("toNameFunction") Function<ItemStack, Component> toNameFunction) {
        super(mod, properties);
        this.toNameFunction = toNameFunction;
    }

    @Override
    public Component getName(ItemStack itemStack) {
        return toNameFunction.apply(itemStack);
    }
}
