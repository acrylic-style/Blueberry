package net.blueberrymc.common.bml.mod;

import net.blueberrymc.client.event.render.LiquidBlockRenderEvent;
import net.blueberrymc.client.event.render.gui.ScreenChangedEvent;
import net.blueberrymc.common.Blueberry;
import net.blueberrymc.common.bml.ModState;
import net.blueberrymc.common.bml.event.EventHandler;
import net.blueberrymc.common.bml.event.Listener;
import net.blueberrymc.world.level.material.MilkFluid;

public class InternalBlueberryModListener implements Listener {
    @EventHandler
    public static void onLiquidBlockRender(LiquidBlockRenderEvent e) {
        if (InternalBlueberryMod.liquidMilk && e.getFluidState().getType().isSame(MilkFluid.Source.INSTANCE)) {
            e.setColor(0xFFFFFF);
        }
    }

    @EventHandler
    public static void onScreenChanged(ScreenChangedEvent e) {
        if (Blueberry.getCurrentState() != ModState.AVAILABLE) return;
        InternalBlueberryMod.refreshDiscordStatus(e.getScreen());
    }
}
