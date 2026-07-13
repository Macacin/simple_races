package net.simpleraces.compat.neoforge.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class NetworkEvent {
	private NetworkEvent() {
	}

	public static final class Context {
		private final IPayloadContext context;

		public Context(IPayloadContext context) {
			this.context = context;
		}

		public CompletableFuture<Void> enqueueWork(Runnable work) {
			return context.enqueueWork(work);
		}

		@Nullable
		public ServerPlayer getSender() {
			if (!context.flow().isServerbound()) {
				return null;
			}
			return (ServerPlayer) context.player();
		}

		public Direction getDirection() {
			return context.flow().isServerbound() ? Direction.PLAY_TO_SERVER : Direction.PLAY_TO_CLIENT;
		}

		public void setPacketHandled(boolean handled) {
		}
	}

	public enum Direction {
		PLAY_TO_SERVER(LogicalSide.SERVER),
		PLAY_TO_CLIENT(LogicalSide.CLIENT);

		private final LogicalSide receptionSide;

		Direction(LogicalSide receptionSide) {
			this.receptionSide = receptionSide;
		}

		public LogicalSide getReceptionSide() {
			return receptionSide;
		}
	}

	public enum LogicalSide {
		CLIENT,
		SERVER;

		public boolean isServer() {
			return this == SERVER;
		}
	}
}




