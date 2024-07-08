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

import net.minecraft.resources.ResourceLocation;

public final class NetworkingImpl {
	/**
	 * Id of packet used to register supported channels.
	 */
	public static final ResourceLocation REGISTER_CHANNEL = new ResourceLocation("fabric", "register");

	/**
	 * Id of packet used to unregister supported channels.
	 */
	public static final ResourceLocation UNREGISTER_CHANNEL = new ResourceLocation("fabric", "unregister");

	public static boolean isReservedCommonChannel(ResourceLocation channelName) {
		return channelName.equals(REGISTER_CHANNEL) || channelName.equals(UNREGISTER_CHANNEL);
	}
}
