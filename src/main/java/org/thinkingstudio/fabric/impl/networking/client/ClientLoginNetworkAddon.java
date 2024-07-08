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

package org.thinkingstudio.fabric.impl.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.thinkingstudio.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import org.thinkingstudio.fabric.api.client.networking.v1.ClientLoginNetworking;
import org.thinkingstudio.fabric.api.networking.v1.PacketByteBufs;
import org.thinkingstudio.fabric.impl.networking.AbstractNetworkAddon;
import org.thinkingstudio.fabric.impl.networking.payload.PacketByteBufLoginQueryRequestPayload;
import org.thinkingstudio.fabric.impl.networking.payload.PacketByteBufLoginQueryResponse;
import org.thinkingstudio.fabric.mixin.networking.client.accessor.ClientLoginNetworkHandlerAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ClientLoginNetworkAddon extends AbstractNetworkAddon<ClientLoginNetworking.LoginQueryRequestHandler> {
	private final ClientHandshakePacketListenerImpl handler;
	private final Minecraft client;
	private boolean firstResponse = true;

	public ClientLoginNetworkAddon(ClientHandshakePacketListenerImpl handler, Minecraft client) {
		super(ClientNetworkingImpl.LOGIN, "ClientLoginNetworkAddon for Client");
		this.handler = handler;
		this.client = client;
	}

	@Override
	protected void invokeInitEvent() {
		ClientLoginConnectionEvents.INIT.invoker().onLoginStart(this.handler, this.client);
	}

	public boolean handlePacket(ClientboundCustomQueryPacket packet) {
		PacketByteBufLoginQueryRequestPayload payload = (PacketByteBufLoginQueryRequestPayload) packet.payload();
		return handlePacket(packet.transactionId(), packet.payload().id(), payload.data());
	}

	private boolean handlePacket(int queryId, ResourceLocation channelName, FriendlyByteBuf originalBuf) {
		this.logger.debug("Handling inbound login response with id {} and channel with name {}", queryId, channelName);

		if (this.firstResponse) {
			ClientLoginConnectionEvents.QUERY_START.invoker().onLoginQueryStart(this.handler, this.client);
			this.firstResponse = false;
		}

		@Nullable ClientLoginNetworking.LoginQueryRequestHandler handler = this.getHandler(channelName);

		if (handler == null) {
			return false;
		}

		FriendlyByteBuf buf = PacketByteBufs.slice(originalBuf);
		List<PacketSendListener> callbacks = new ArrayList<>();

		try {
			CompletableFuture<@Nullable FriendlyByteBuf> future = handler.receive(this.client, this.handler, buf, callbacks::add);
			future.thenAccept(result -> {
				ServerboundCustomQueryAnswerPacket packet = new ServerboundCustomQueryAnswerPacket(queryId, result == null ? null : new PacketByteBufLoginQueryResponse(result));
				((ClientLoginNetworkHandlerAccessor) this.handler).getConnection().send(packet, new PacketSendListener() {
					@Override
					public void onSuccess() {
						callbacks.forEach(PacketSendListener::onSuccess);
					}
				});
			});
		} catch (Throwable ex) {
			this.logger.error("Encountered exception while handling in channel with name \"{}\"", channelName, ex);
			throw ex;
		}

		return true;
	}

	@Override
	protected void handleRegistration(ResourceLocation channelName) {
	}

	@Override
	protected void handleUnregistration(ResourceLocation channelName) {
	}

	@Override
	protected void invokeDisconnectEvent() {
		ClientLoginConnectionEvents.DISCONNECT.invoker().onLoginDisconnect(this.handler, this.client);
	}

	@Override
	protected boolean isReservedChannel(ResourceLocation channelName) {
		return false;
	}
}
