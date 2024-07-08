package org.thinkingstudio.neonetwork.impl.server;

import org.thinkingstudio.fabric.api.networking.v1.PacketSender;
import org.thinkingstudio.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record NeoServerPacketSender(Connection connection) implements PacketSender {
    @Override
    public Packet<?> createPacket(CustomPacketPayload packet) {
        return ServerPlayNetworking.createS2CPacket(packet);
    }

    @Override
    public void sendPacket(Packet<?> packet, @Nullable PacketSendListener callback) {
        Objects.requireNonNull(packet, "Packet cannot be null");

        this.connection.send(packet, callback);
    }

    @Override
    public void disconnect(Component disconnectReason) {
        Objects.requireNonNull(disconnectReason, "Disconnect reason cannot be null");

        this.connection.disconnect(disconnectReason);
    }
}
