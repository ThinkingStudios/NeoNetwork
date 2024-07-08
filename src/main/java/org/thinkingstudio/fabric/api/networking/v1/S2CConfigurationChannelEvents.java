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

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.thinkingstudio.fabric.api.event.Event;
import org.thinkingstudio.fabric.api.event.EventFactory;

import java.util.List;

/**
 * Offers access to events related to the indication of a connected client's ability to receive packets in certain channels.
 */
public final class S2CConfigurationChannelEvents {
	/**
	 * An event for the server configuration network handler receiving an update indicating the connected client's ability to receive packets in certain channels.
	 * This event may be invoked at any time after login and up to disconnection.
	 */
	public static final Event<Register> REGISTER = EventFactory.createArrayBacked(Register.class, callbacks -> (handler, sender, server, channels) -> {
		for (Register callback : callbacks) {
			callback.onChannelRegister(handler, sender, server, channels);
		}
	});

	/**
	 * An event for the server configuration network handler receiving an update indicating the connected client's lack of ability to receive packets in certain channels.
	 * This event may be invoked at any time after login and up to disconnection.
	 */
	public static final Event<Unregister> UNREGISTER = EventFactory.createArrayBacked(Unregister.class, callbacks -> (handler, sender, server, channels) -> {
		for (Unregister callback : callbacks) {
			callback.onChannelUnregister(handler, sender, server, channels);
		}
	});

	private S2CConfigurationChannelEvents() {
	}

	/**
	 * @see S2CConfigurationChannelEvents#REGISTER
	 */
	@FunctionalInterface
	public interface Register {
		void onChannelRegister(ServerConfigurationPacketListenerImpl handler, PacketSender sender, MinecraftServer server, List<ResourceLocation> channels);
	}

	/**
	 * @see S2CConfigurationChannelEvents#UNREGISTER
	 */
	@FunctionalInterface
	public interface Unregister {
		void onChannelUnregister(ServerConfigurationPacketListenerImpl handler, PacketSender sender, MinecraftServer server, List<ResourceLocation> channels);
	}
}
