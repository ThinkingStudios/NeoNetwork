package org.thinkingstudio.neonetwork.impl.server;

import org.thinkingstudio.fabric.api.networking.v1.PacketSender;
import org.thinkingstudio.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.thinkingstudio.fabric.api.networking.v1.ServerPlayNetworking;
import org.thinkingstudio.fabric.impl.networking.PayloadTypeRegistryImpl;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.thinkingstudio.neonetwork.impl.NeoCommonNetworking;

import java.util.Set;

public class NeoServerPlayNetworking {
    public static <T extends CustomPacketPayload> boolean registerGlobalReceiver(CustomPacketPayload.Type<T> type, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        NeoCommonNetworking.assertPayloadType(PayloadTypeRegistryImpl.PLAY_C2S, type.id(), PacketFlow.SERVERBOUND, ConnectionProtocol.PLAY);
        return NeoCommonNetworking.PLAY_REGISTRY.registerGlobalReceiver(type, PacketFlow.SERVERBOUND, handler, ServerNeoContextWrapper::new, ServerPlayNetworking.PlayPayloadHandler::receive);
    }

    public static ServerPlayNetworking.PlayPayloadHandler<?> unregisterGlobalReceiver(ResourceLocation id) {
        return NeoCommonNetworking.PLAY_REGISTRY.unregisterGlobalReceiver(id, PacketFlow.SERVERBOUND);
    }

    public static Set<ResourceLocation> getGlobalReceivers() {
        return NeoCommonNetworking.PLAY_REGISTRY.getGlobalReceivers(PacketFlow.SERVERBOUND);
    }

    public static <T extends CustomPacketPayload> boolean registerReceiver(ServerGamePacketListenerImpl networkHandler, CustomPacketPayload.Type<T> type, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        NeoCommonNetworking.assertPayloadType(PayloadTypeRegistryImpl.PLAY_C2S, type.id(), PacketFlow.SERVERBOUND, ConnectionProtocol.PLAY);
        return NeoCommonNetworking.PLAY_REGISTRY.registerLocalReceiver(type, networkHandler, handler, ServerNeoContextWrapper::new, ServerPlayNetworking.PlayPayloadHandler::receive);
    }

    public static ServerPlayNetworking.PlayPayloadHandler<?> unregisterReceiver(ServerGamePacketListenerImpl networkHandler, ResourceLocation id) {
        return NeoCommonNetworking.PLAY_REGISTRY.unregisterLocalReceiver(id, networkHandler);
    }

    public static Set<ResourceLocation> getReceived(ServerGamePacketListenerImpl handler) throws IllegalStateException {
        return NeoCommonNetworking.PLAY_REGISTRY.getLocalReceivers(handler);
    }

    public static Set<ResourceLocation> getSendable(ServerGamePacketListenerImpl handler) throws IllegalStateException {
        return NeoCommonNetworking.PLAY_REGISTRY.getLocalSendable(handler);
    }

    public static boolean canSend(ServerGamePacketListenerImpl handler, ResourceLocation channelName) throws IllegalArgumentException {
        return NetworkRegistry.hasChannel(handler, channelName);
    }

    public static PacketSender getSender(ServerGamePacketListenerImpl handler) {
        return new NeoServerPacketSender(handler.getConnection());
    }

    public static void onClientReady(ServerPlayer player) {
        ServerPlayConnectionEvents.JOIN.invoker().onPlayReady(player.connection, new NeoServerPacketSender(player.connection.getConnection()), player.server);
    }

    private record ServerNeoContextWrapper(IPayloadContext context) implements ServerPlayNetworking.Context {
        @Override
        public MinecraftServer server() {
            return player().getServer();
        }

        @Override
        public ServerPlayer player() {
            return (ServerPlayer) context.player();
        }

        @Override
        public PacketSender responseSender() {
            return new NeoServerPacketSender(context.connection());
        }
    }
}
