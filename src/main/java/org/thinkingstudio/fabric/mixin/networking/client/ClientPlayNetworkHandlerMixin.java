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

package org.thinkingstudio.fabric.mixin.networking.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.thinkingstudio.neonetwork.impl.NeoListenableNetworkHandler;
import org.thinkingstudio.neonetwork.impl.client.NeoClientPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.thinkingstudio.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

// We want to apply a bit earlier than other mods which may not use us in order to prevent refCount issues
@Mixin(value = ClientPacketListener.class, priority = 999)
abstract class ClientPlayNetworkHandlerMixin extends ClientCommonPacketListenerImpl implements NeoListenableNetworkHandler {
    protected ClientPlayNetworkHandlerMixin(Minecraft client, Connection connection, CommonListenerCookie connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initAddon(CallbackInfo ci) {
        NeoClientPlayNetworking.setTempPacketListener((ClientPacketListener) (Object) this);
        ClientPlayConnectionEvents.INIT.invoker().onPlayInit((ClientPacketListener) (Object) this, this.minecraft);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void handleServerPlayReady(ClientboundLoginPacket packet, CallbackInfo ci) {
        NeoClientPlayNetworking.onServerReady((ClientPacketListener) (Object) this, this.minecraft);
    }

    @Override
    public void handleDisconnect() {
        ClientPlayConnectionEvents.DISCONNECT.invoker().onPlayDisconnect((ClientPacketListener) (Object) this, this.minecraft);
    }
}
