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

package org.thinkingstudio.fabric.impl.networking.server;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.thinkingstudio.fabric.api.networking.v1.ServerLoginNetworking;
import org.thinkingstudio.fabric.impl.networking.GlobalReceiverRegistry;
import org.thinkingstudio.fabric.impl.networking.NetworkHandlerExtensions;

import java.util.Objects;

public final class ServerNetworkingImpl {
	public static final GlobalReceiverRegistry<ServerLoginNetworking.LoginQueryResponseHandler> LOGIN = new GlobalReceiverRegistry<>(PacketFlow.SERVERBOUND, ConnectionProtocol.LOGIN, null);

	public static ServerLoginNetworkAddon getAddon(ServerLoginPacketListenerImpl handler) {
		return (ServerLoginNetworkAddon) ((NetworkHandlerExtensions) handler).getAddon();
	}

	public static Packet<ClientCommonPacketListener> createS2CPacket(CustomPacketPayload payload) {
		Objects.requireNonNull(payload, "Payload cannot be null");
		Objects.requireNonNull(payload.type(), "CustomPayload#getId() cannot return null for payload class: " + payload.getClass());

		return new ClientboundCustomPayloadPacket(payload);
	}
}
