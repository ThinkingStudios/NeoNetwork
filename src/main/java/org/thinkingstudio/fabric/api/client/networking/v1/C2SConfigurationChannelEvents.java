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
import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.resources.ResourceLocation;
import org.thinkingstudio.fabric.api.event.Event;
import org.thinkingstudio.fabric.api.event.EventFactory;
import org.thinkingstudio.fabric.api.networking.v1.PacketSender;

import java.util.List;

/**
 * Offers access to events related to the indication of a connected server's ability to receive packets in certain channels.
 */
public final class C2SConfigurationChannelEvents {
	/**
	 * An event for the client configuration network handler receiving an update indicating the connected server's ability to receive packets in certain channels.
	 * This event may be invoked at any time after login and up to disconnection.
	 */
	public static final Event<Register> REGISTER = EventFactory.createArrayBacked(Register.class, callbacks -> (handler, sender, client, channels) -> {
		for (Register callback : callbacks) {
			callback.onChannelRegister(handler, sender, client, channels);
		}
	});

	/**
	 * An event for the client configuration network handler receiving an update indicating the connected server's lack of ability to receive packets in certain channels.
	 * This event may be invoked at any time after login and up to disconnection.
	 */
	public static final Event<Unregister> UNREGISTER = EventFactory.createArrayBacked(Unregister.class, callbacks -> (handler, sender, client, channels) -> {
		for (Unregister callback : callbacks) {
			callback.onChannelUnregister(handler, sender, client, channels);
		}
	});

	private C2SConfigurationChannelEvents() {
	}

	/**
	 * @see C2SConfigurationChannelEvents#REGISTER
	 */
	@FunctionalInterface
	public interface Register {
		void onChannelRegister(ClientConfigurationPacketListenerImpl handler, PacketSender sender, Minecraft client, List<ResourceLocation> channels);
	}

	/**
	 * @see C2SConfigurationChannelEvents#UNREGISTER
	 */
	@FunctionalInterface
	public interface Unregister {
		void onChannelUnregister(ClientConfigurationPacketListenerImpl handler, PacketSender sender, Minecraft client, List<ResourceLocation> channels);
	}
}
