package org.thinkingstudio.neonetwork;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.thinkingstudio.neonetwork.impl.NetworkingEventHooks;

@Mod(NeoNetworkMod.MOD_ID)
public final class NeoNetworkMod {
    public static final String MOD_ID = "neonetwork";

    public NeoNetworkMod(IEventBus modEventBus) {
        NetworkingEventHooks.registerEvents(modEventBus);
    }
}
