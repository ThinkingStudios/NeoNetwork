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

import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Represents something that supports sending packets to channels.
 * Any packets sent must be {@linkplain PayloadTypeRegistry registered} in the appropriate registry.
 */
@ApiStatus.NonExtendable
public interface PacketSender {
	/**
	 * Creates a packet from a packet payload.
	 *
	 * @param payload the packet payload
	 */
	Packet<?> createPacket(CustomPacketPayload payload);

	/**
	 * Sends a packet.
	 *
	 * @param packet the packet
	 */
	default void sendPacket(Packet<?> packet) {
		sendPacket(packet, null);
	}

	/**
	 * Sends a packet.
	 * @param payload the payload
	 */
	default void sendPacket(CustomPacketPayload payload) {
		sendPacket(createPacket(payload));
	}

	/**
	 * Sends a packet.
	 *
	 * @param packet the packet
	 * @param callback an optional callback to execute after the packet is sent, may be {@code null}.
	 */
	void sendPacket(Packet<?> packet, @Nullable PacketSendListener callback);

	/**
	 * Sends a packet.
	 *
	 * @param payload the payload
	 * @param callback an optional callback to execute after the packet is sent, may be {@code null}.
	 */
	default void sendPacket(CustomPacketPayload payload, @Nullable PacketSendListener callback) {
		sendPacket(createPacket(payload), callback);
	}

	/**
	 * Disconnects the player.
	 * @param disconnectReason the reason for disconnection
	 */
	void disconnect(Component disconnectReason);
}
