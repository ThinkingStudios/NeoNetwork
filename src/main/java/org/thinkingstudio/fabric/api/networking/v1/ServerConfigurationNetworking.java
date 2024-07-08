/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.thinkingstudio.fabric.api.networking.v1;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.thinkingstudio.neonetwork.impl.server.NeoServerConfigurationNetworking;
import org.thinkingstudio.fabric.impl.networking.server.ServerNetworkingImpl;
import org.thinkingstudio.fabric.mixin.networking.accessor.ServerCommonNetworkHandlerAccessor;

import java.util.Objects;
import java.util.Set;

/**
 * Offers access to configuration stage server-side networking functionalities.
 *
 * <p>Server-side networking functionalities include receiving serverbound packets, sending clientbound packets, and events related to server-side network handlers.
 * Packets <strong>received</strong> by this class must be registered to {@link PayloadTypeRegistry#configurationC2S()} on both ends.
 * Packets <strong>sent</strong> by this class must be registered to {@link PayloadTypeRegistry#configurationS2C()} on both ends.
 * Packets must be registered before registering any receivers.
 *
 * <p>This class should be only used for the logical server.
 *
 * <p>See {@link ServerPlayNetworking} for information on sending and receiving play phase packets.
 *
 * <p>See the documentation on each class for more information.
 *
 * @see ServerLoginNetworking
 * @see ServerConfigurationNetworking
 */
public final class ServerConfigurationNetworking {
	/**
	 * Registers a handler for a payload type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(ServerConfigurationPacketListenerImpl, ResourceLocation)} to unregister the existing handler.
	 *
	 * @param type the packet type
	 * @param handler the handler
	 * @return {@code false} if a handler is already registered to the channel
	 * @throws IllegalArgumentException if the codec for {@code type} has not been {@linkplain PayloadTypeRegistry#configurationC2S() registered} yet
	 * @see ServerConfigurationNetworking#unregisterGlobalReceiver(ResourceLocation)
	 * @see ServerConfigurationNetworking#registerReceiver(ServerConfigurationPacketListenerImpl, CustomPacketPayload.Type, ConfigurationPacketHandler)
	 */
	public static <T extends CustomPacketPayload> boolean registerGlobalReceiver(CustomPacketPayload.Type<T> type, ConfigurationPacketHandler<T> handler) {
		return NeoServerConfigurationNetworking.registerGlobalReceiver(type, handler);
	}

	/**
	 * Removes the handler for a payload type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param id the packet payload id
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerGlobalReceiver(CustomPacketPayload.Type, ConfigurationPacketHandler)}
	 * @see ServerConfigurationNetworking#registerGlobalReceiver(CustomPacketPayload.Type, ConfigurationPacketHandler)
	 * @see ServerConfigurationNetworking#unregisterReceiver(ServerConfigurationPacketListenerImpl, ResourceLocation)
	 */
	@Nullable
	public static ServerConfigurationNetworking.ConfigurationPacketHandler<?> unregisterGlobalReceiver(ResourceLocation id) {
		return NeoServerConfigurationNetworking.unregisterGlobalReceiver(id);
	}

	/**
	 * Gets all channel names which global receivers are registered for.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * @return all channel names which global receivers are registered for.
	 */
	public static Set<ResourceLocation> getGlobalReceivers() {
		return NeoServerConfigurationNetworking.getGlobalReceivers();
	}

	/**
	 * Registers a handler for a payload type.
	 * This method differs from {@link ServerConfigurationNetworking#registerGlobalReceiver(CustomPacketPayload.Type, ConfigurationPacketHandler)} since
	 * the channel handler will only be applied to the client represented by the {@link ServerConfigurationPacketListenerImpl}.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(ServerConfigurationPacketListenerImpl, ResourceLocation)} to unregister the existing handler.
	 *
	 * @param networkHandler the network handler
	 * @param type the packet type
	 * @param handler the handler
	 * @return {@code false} if a handler is already registered to the channel name
	 * @throws IllegalArgumentException if the codec for {@code type} has not been {@linkplain PayloadTypeRegistry#configurationC2S() registered} yet
	 * @see ServerPlayConnectionEvents#INIT
	 */
	public static <T extends CustomPacketPayload> boolean registerReceiver(ServerConfigurationPacketListenerImpl networkHandler, CustomPacketPayload.Type<T> type, ConfigurationPacketHandler<T> handler) {
		return NeoServerConfigurationNetworking.registerReceiver(networkHandler, type, handler);
	}

	/**
	 * Removes the handler for a payload type.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param id the id of the payload
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerReceiver(ServerConfigurationPacketListenerImpl, CustomPacketPayload.Type, ConfigurationPacketHandler)}
	 */
	@Nullable
	public static ServerConfigurationNetworking.ConfigurationPacketHandler<?> unregisterReceiver(ServerConfigurationPacketListenerImpl networkHandler, ResourceLocation id) {
		return NeoServerConfigurationNetworking.unregisterReceiver(networkHandler, id);
	}

	/**
	 * Gets all the channel names that the server can receive packets on.
	 *
	 * @param handler the network handler
	 * @return All the channel names that the server can receive packets on
	 */
	public static Set<ResourceLocation> getReceived(ServerConfigurationPacketListenerImpl handler) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");

		return NeoServerConfigurationNetworking.getReceived(handler);
	}

	/**
	 * Gets all channel names that a connected client declared the ability to receive a packets on.
	 *
	 * @param handler the network handler
	 * @return {@code true} if the connected client has declared the ability to receive a packet on the specified channel
	 */
	public static Set<ResourceLocation> getSendable(ServerConfigurationPacketListenerImpl handler) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");

		return NeoServerConfigurationNetworking.getSendable(handler);
	}

	/**
	 * Checks if the connected client declared the ability to receive a packet on a specified channel name.
	 *
	 * @param handler the network handler
	 * @param channelName the channel name
	 * @return {@code true} if the connected client has declared the ability to receive a packet on the specified channel
	 */
	public static boolean canSend(ServerConfigurationPacketListenerImpl handler, ResourceLocation channelName) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");
		Objects.requireNonNull(channelName, "Channel name cannot be null");

		return NeoServerConfigurationNetworking.canSend(handler, channelName);
	}

	/**
	 * Checks if the connected client declared the ability to receive a specific type of packet.
	 *
	 * @param handler the network handler
	 * @param id the payload id
	 * @return {@code true} if the connected client has declared the ability to receive a specific type of packet
	 */
	public static boolean canSend(ServerConfigurationPacketListenerImpl handler, CustomPacketPayload.Type<?> id) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");
		Objects.requireNonNull(id, "Payload id cannot be null");

		return NeoServerConfigurationNetworking.canSend(handler, id.id());
	}

	/**
	 * Creates a packet which may be sent to a connected client.
	 *
	 * @param payload the payload
	 * @return a new packet
	 */
	public static Packet<ClientCommonPacketListener> createS2CPacket(CustomPacketPayload payload) {
		Objects.requireNonNull(payload, "Payload cannot be null");
		Objects.requireNonNull(payload.type(), "CustomPayload#getId() cannot return null for payload class: " + payload.getClass());

		return ServerNetworkingImpl.createS2CPacket(payload);
	}

	/**
	 * Gets the packet sender which sends packets to the connected client.
	 *
	 * @param handler the network handler, representing the connection to the player/client
	 * @return the packet sender
	 */
	public static PacketSender getSender(ServerConfigurationPacketListenerImpl handler) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");

		return NeoServerConfigurationNetworking.getSender(handler);
	}

	/**
	 * Sends a packet to a configuring player.
	 *
	 * <p>Any packets sent must be {@linkplain PayloadTypeRegistry#configurationS2C() registered}.</p>
	 *
	 * @param handler the network handler to send the packet to
	 * @param payload to be sent
	 */
	public static void send(ServerConfigurationPacketListenerImpl handler, CustomPacketPayload payload) {
		Objects.requireNonNull(handler, "Server configuration handler cannot be null");
		Objects.requireNonNull(payload, "Payload cannot be null");
		Objects.requireNonNull(payload.type(), "CustomPayload#getId() cannot return null for payload class: " + payload.getClass());

		handler.send(createS2CPacket(payload));
	}

	// Helper methods

	/**
	 * Returns the <i>Minecraft</i> Server of a server configuration network handler.
	 *
	 * @param handler the server configuration network handler
	 */
	public static MinecraftServer getServer(ServerConfigurationPacketListenerImpl handler) {
		Objects.requireNonNull(handler, "Network handler cannot be null");

		return ((ServerCommonNetworkHandlerAccessor) handler).getServer();
	}

	private ServerConfigurationNetworking() {
	}

	/**
	 * A packet handler utilizing {@link CustomPacketPayload}.
	 * @param <T> the type of the packet
	 */
	@FunctionalInterface
	public interface ConfigurationPacketHandler<T extends CustomPacketPayload> {
		/**
		 * Handles an incoming packet.
		 *
		 * <p>Unlike {@link ServerPlayNetworking.PlayPayloadHandler} this method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
		 * Modification to the game should be {@linkplain BlockableEventLoop#submit(Runnable) scheduled} using the Minecraft server instance from {@link ServerConfigurationNetworking#getServer(ServerConfigurationPacketListenerImpl)}.
		 *
		 * <p>An example usage of this:
		 * <pre>{@code
		 * // use PayloadTypeRegistry for registering the payload
		 * ServerConfigurationNetworking.registerReceiver(BOOM_PACKET_TYPE, (payload, context) -> {
		 *
		 * });
		 * }</pre>
		 *
		 *
		 * @param payload the packet payload
		 * @param context the configuration networking context
		 * @see CustomPacketPayload
		 */
		void receive(T payload, Context context);
	}

	@ApiStatus.NonExtendable
	public interface Context {
		/**
		 * @return The MinecraftServer instance
		 */
		MinecraftServer server();

		/**
		 * @return The ServerConfigurationNetworkHandler instance
		 */
		ServerConfigurationPacketListenerImpl networkHandler();

		/**
		 * @return The packet sender
		 */
		PacketSender responseSender();
	}
}
