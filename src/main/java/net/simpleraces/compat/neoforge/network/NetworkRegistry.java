package net.simpleraces.compat.neoforge.network;

import net.minecraft.resources.ResourceLocation;
import net.simpleraces.compat.neoforge.network.simple.SimpleChannel;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class NetworkRegistry {
	private NetworkRegistry() {
	}

	public static SimpleChannel newSimpleChannel(ResourceLocation name, Supplier<String> versionSupplier, Predicate<String> clientAccepted, Predicate<String> serverAccepted) {
		return new SimpleChannel(name);
	}
}




