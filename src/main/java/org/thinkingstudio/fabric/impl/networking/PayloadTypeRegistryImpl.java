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

package org.thinkingstudio.fabric.impl.networking;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.thinkingstudio.fabric.api.networking.v1.PayloadTypeRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PayloadTypeRegistryImpl<B extends FriendlyByteBuf> implements PayloadTypeRegistry<B> {
	public static final PayloadTypeRegistryImpl<FriendlyByteBuf> CONFIGURATION_C2S = new PayloadTypeRegistryImpl<>(ConnectionProtocol.CONFIGURATION, PacketFlow.SERVERBOUND);
	public static final PayloadTypeRegistryImpl<FriendlyByteBuf> CONFIGURATION_S2C = new PayloadTypeRegistryImpl<>(ConnectionProtocol.CONFIGURATION, PacketFlow.CLIENTBOUND);
	public static final PayloadTypeRegistryImpl<RegistryFriendlyByteBuf> PLAY_C2S = new PayloadTypeRegistryImpl<>(ConnectionProtocol.PLAY, PacketFlow.SERVERBOUND);
	public static final PayloadTypeRegistryImpl<RegistryFriendlyByteBuf> PLAY_S2C = new PayloadTypeRegistryImpl<>(ConnectionProtocol.PLAY, PacketFlow.CLIENTBOUND);

	private final Map<ResourceLocation, CustomPacketPayload.TypeAndCodec<B, ? extends CustomPacketPayload>> packetTypes = new HashMap<>();
	private final ConnectionProtocol state;
	private final PacketFlow side;

	private PayloadTypeRegistryImpl(ConnectionProtocol state, PacketFlow side) {
		this.state = state;
		this.side = side;
	}

	@Override
	public <T extends CustomPacketPayload> CustomPacketPayload.TypeAndCodec<? super B, T> register(CustomPacketPayload.Type<T> id, StreamCodec<? super B, T> codec) {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(codec, "codec");

		final CustomPacketPayload.TypeAndCodec<B, T> payloadType = new CustomPacketPayload.TypeAndCodec<>(id, codec.cast());

		if (packetTypes.containsKey(id.id())) {
			throw new IllegalArgumentException("Packet type " + id + " is already registered!");
		}

		packetTypes.put(id.id(), payloadType);
		return payloadType;
	}

	@Nullable
	public CustomPacketPayload.TypeAndCodec<B, ? extends CustomPacketPayload> get(ResourceLocation id) {
		return packetTypes.get(id);
	}

	@Nullable
	public <T extends CustomPacketPayload> CustomPacketPayload.TypeAndCodec<B, T> get(CustomPacketPayload.Type<T> id) {
		//noinspection unchecked
		return (CustomPacketPayload.TypeAndCodec<B, T>) packetTypes.get(id.id());
	}

	public ConnectionProtocol getPhase() {
		return state;
	}

	public PacketFlow getSide() {
		return side;
	}
}
