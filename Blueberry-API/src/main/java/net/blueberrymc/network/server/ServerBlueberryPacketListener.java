package net.blueberrymc.network.server;

import net.blueberrymc.network.BlueberryNetworkManager;
import net.blueberrymc.network.BlueberryPacket;
import net.blueberrymc.network.BlueberryPacketListener;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import org.jetbrains.annotations.NotNull;

public interface ServerBlueberryPacketListener extends BlueberryPacketListener {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    default boolean handleBlueberryCustomPayload(@NotNull ServerboundCustomPayloadPacket packet) {
        BlueberryPacket<?> blueberryPacket = BlueberryNetworkManager.handle(packet);
        if (blueberryPacket != null) {
            ((BlueberryPacket) blueberryPacket).handle(this);
            return true;
        }
        return false;
    }
}
