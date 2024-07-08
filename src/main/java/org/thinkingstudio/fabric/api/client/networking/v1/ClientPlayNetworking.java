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

package org.thinkingstudio.fabric.api.client.networking.v1;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.thinkingstudio.neonetwork.impl.client.NeoClientPlayNetworking;
import org.thinkingstudio.fabric.api.networking.v1.PacketSender;
import org.thinkingstudio.fabric.api.networking.v1.PayloadTypeRegistry;
import org.thinkingstudio.fabric.api.networking.v1.ServerPlayNetworking;
import org.thinkingstudio.fabric.impl.networking.client.ClientNetworkingImpl;

import java.util.Objects;
import java.util.Set;

/**
 * Offers access to play stage client-side networking functionalities.
 *
 * <p>Client-side networking functionalities include receiving clientbound packets,
 * sending serverbound packets, and events related to client-side network handlers.
 * Packets <strong>received</strong> by this class must be registered to {@link PayloadTypeRegistry#playS2C()} on both ends.
 * Packets <strong>sent</strong> by this class must be registered to {@link PayloadTypeRegistry#playC2S()} on both ends.
 * Packets must be registered before registering any receivers.
 *
 * <p>This class should be only used on the physical client and for the logical client.
 *
 * <p>See {@link ServerPlayNetworking} for information on how to use the payload
 * object-based API.
 *
 * @see ClientLoginNetworking
 * @see ClientConfigurationNetworking
 * @see ServerPlayNetworking
 */
public final class ClientPlayNetworking {
	/**
	 * Registers a handler for a payload type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterGlobalReceiver(ResourceLocation)} to unregister the existing handler.
	 *
	 * @param type the payload type
	 * @param handler the handler
	 * @return false if a handler is already registered to the channel
	 * @throws IllegalArgumentException if the codec for {@code type} has not been {@linkplain PayloadTypeRegistry#playS2C() registered} yet
	 * @see ClientPlayNetworking#unregisterGlobalReceiver(ResourceLocation)
	 * @see ClientPlayNetworking#registerReceiver(CustomPacketPayload.Type, PlayPayloadHandler)
	 */
	public static <T extends CustomPacketPayload> boolean registerGlobalReceiver(CustomPacketPayload.Type<T> type, PlayPayloadHandler<T> handler) {
		return NeoClientPlayNetworking.registerGlobalReceiver(type, handler);
	}

	/**
	 * Removes the handler for a payload type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param id the payload id
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerGlobalReceiver(CustomPacketPayload.Type, PlayPayloadHandler)}
	 * @see ClientPlayNetworking#registerGlobalReceiver(CustomPacketPayload.Type, PlayPayloadHandler)
	 * @see ClientPlayNetworking#unregisterReceiver(ResourceLocation)
	 */
	@Nullable
	public static ClientPlayNetworking.PlayPayloadHandler<?> unregisterGlobalReceiver(ResourceLocation id) {
		return NeoClientPlayNetworking.unregisterGlobalReceiver(id);
	}

	/**
	 * Gets all channel names which global receivers are registered for.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * @return all channel names which global receivers are registered for.
	 */
	public static Set<ResourceLocation> getGlobalReceivers() {
		return NeoClientPlayNetworking.getGlobalReceivers();
	}

	/**
	 * Registers a handler for a payload type.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(ResourceLocation)} to unregister the existing handler.
	 *
	 * <p>For example, if you only register a receiver using this method when a {@linkplain ClientLoginNetworking#registerGlobalReceiver(ResourceLocation, ClientLoginNetworking.LoginQueryRequestHandler)}
	 * login query has been received, you should use {@link ClientPlayConnectionEvents#INIT} to register the channel handler.
	 *
	 * @param type the payload type
	 * @param handler the handler
	 * @return {@code false} if a handler is already registered for the type
	 * @throws IllegalArgumentException if the codec for {@code type} has not been {@linkplain PayloadTypeRegistry#playS2C() registered} yet
	 * @throws IllegalStateException if the client is not connected to a server
	 * @see ClientPlayConnectionEvents#INIT
	 */
	public static <T extends CustomPacketPayload> boolean registerReceiver(CustomPacketPayload.Type<T> type, PlayPayloadHandler<T> handler) {
		return NeoClientPlayNetworking.registerReceiver(type, handler);
	}

	/**
	 * Removes the handler for a payload id.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param id the payload id
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerReceiver(CustomPacketPayload.Type, PlayPayloadHandler)}
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	@Nullable
	public static ClientPlayNetworking.PlayPayloadHandler<?> unregisterReceiver(ResourceLocation id) {
		return NeoClientPlayNetworking.unregisterReceiver(id);
	}

	/**
	 * Gets all the channel names that the client can receive packets on.
	 *
	 * @return All the channel names that the client can receive packets on
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static Set<ResourceLocation> getReceived() throws IllegalStateException {
		return NeoClientPlayNetworking.getReceived();
	}

	/**
	 * Gets all channel names that the connected server declared the ability to receive a packets on.
	 *
	 * @return All the channel names the connected server declared the ability to receive a packets on
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static Set<ResourceLocation> getSendable() throws IllegalStateException {
		return NeoClientPlayNetworking.getSendable();
	}

	/**
	 * Checks if the connected server declared the ability to receive a payload on a specified channel name.
	 *
	 * @param channelName the channel name
	 * @return {@code true} if the connected server has declared the ability to receive a payload on the specified channel.
	 * False if the client is not in game.
	 */
	public static boolean canSend(ResourceLocation channelName) throws IllegalArgumentException {
		// You cant send without a client player, so this is fine
		if (Minecraft.getInstance().getConnection() != null) {
			return NeoClientPlayNetworking.canSend(channelName);
		}

		return false;
	}

	/**
	 * Checks if the connected server declared the ability to receive a payload on a specified channel name.
	 * This returns {@code false} if the client is not in game.
	 *
	 * @param type the payload type
	 * @return {@code true} if the connected server has declared the ability to receive a payload on the specified channel
	 */
	public static boolean canSend(CustomPacketPayload.Type<?> type) {
		return canSend(type.id());
	}

	/**
	 * Creates a payload which may be sent to the connected server.
	 *
	 * @param packet the fabric payload
	 * @return a new payload
	 */
	public static <T extends CustomPacketPayload> Packet<ServerCommonPacketListener> createC2SPacket(T packet) {
		return ClientNetworkingImpl.createC2SPacket(packet);
	}

	/**
	 * Gets the payload sender which sends packets to the connected server.
	 *
	 * @return the client's payload sender
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static PacketSender getSender() throws IllegalStateException {
		return NeoClientPlayNetworking.getSender();
	}

	/**
	 * Sends a payload to the connected server.
	 *
	 * <p>Any packets sent must be {@linkplain PayloadTypeRegistry#playC2S() registered}.</p>
	 *
	 * @param payload the payload
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static void send(CustomPacketPayload payload) {
		Objects.requireNonNull(payload, "Payload cannot be null");
		Objects.requireNonNull(payload.type(), "CustomPayload#getId() cannot return null for payload class: " + payload.getClass());

		// You cant send without a client player, so this is fine
		if (Minecraft.getInstance().getConnection() != null) {
			Minecraft.getInstance().getConnection().send(createC2SPacket(payload));
			return;
		}

		throw new IllegalStateException("Cannot send packets when not in game!");
	}

	private ClientPlayNetworking() {
	}

	/**
	 * A thread-safe payload handler utilizing {@link CustomPacketPayload}.
	 * @param <T> the type of the payload
	 */
	@FunctionalInterface
	public interface PlayPayloadHandler<T extends CustomPacketPayload> {
		/**
		 * Handles the incoming payload. This is called on the render thread, and can safely
		 * call client methods.
		 *
		 * <p>An example usage of this is to display an overlay message:
		 * <pre>{@code
		 * // use PayloadTypeRegistry for registering the payload
		 * ClientPlayNetworking.registerReceiver(OVERLAY_PACKET_TYPE, (payload, context) -> {
		 * 	context.client().inGameHud.setOverlayMessage(payload.message(), true);
		 * });
		 * }</pre>
		 *
		 * <p>The network handler can be accessed via {@link LocalPlayer#connection}.
		 *
		 * @param payload the packet payload
		 * @param context the play networking context
		 * @see CustomPacketPayload
		 */
		void receive(T payload, Context context);
	}

	@ApiStatus.NonExtendable
	public interface Context {
		/**
		 * @return The MinecraftClient instance
		 */
		Minecraft client();

		/**
		 * @return The player that received the payload
		 */
		LocalPlayer player();

		/**
		 * @return The packet sender
		 */
		PacketSender responseSender();
	}
}
