package net.simpleraces.compat.neoforge.network.simple;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.simpleraces.compat.neoforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleChannel {
	private final ResourceLocation name;
	private final CustomPacketPayload.Type<CompatPayload> payloadType;
	private final StreamCodec<RegistryFriendlyByteBuf, CompatPayload> payloadCodec;
	private final Map<Integer, Registration<?>> registrationsById = new HashMap<>();
	private final Map<Class<?>, Registration<?>> registrationsByClass = new HashMap<>();
	private boolean registered;

	public SimpleChannel(ResourceLocation name) {
		this.name = Objects.requireNonNull(name, "name");
		this.payloadType = new CustomPacketPayload.Type<>(
				ResourceLocation.fromNamespaceAndPath(name.getNamespace(), name.getPath() + "_compat")
		);
		this.payloadCodec = createPayloadCodec(this.payloadType);
	}

	public <T> void registerMessage(int id, Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
	                                BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
		Registration<T> registration = new Registration<>(id, type, encoder, decoder, handler);
		registrationsById.put(id, registration);
		registrationsByClass.put(type, registration);
	}

	public void register(IEventBus modBus) {
		if (registered) {
			return;
		}
		registered = true;
		modBus.addListener(this::registerPayloads);
	}

	public void sendToServer(Object message) {
		PacketDistributor.sendToServer(toPayload(message));
	}

	public void sendToPlayer(ServerPlayer player, Object message) {
		PacketDistributor.sendToPlayer(player, toPayload(message));
	}

	public void sendToDimension(ServerLevel level, Object message) {
		PacketDistributor.sendToPlayersInDimension(level, toPayload(message));
	}

	private void registerPayloads(RegisterPayloadHandlersEvent event) {
		event.registrar(name.toString()).playBidirectional(payloadType, payloadCodec,
				new DirectionalPayloadHandler<>(this::handleClientbound, this::handleServerbound));
	}

	private void handleClientbound(CompatPayload payload, IPayloadContext context) {
		handlePayload(payload, context);
	}

	private void handleServerbound(CompatPayload payload, IPayloadContext context) {
		handlePayload(payload, context);
	}

	@SuppressWarnings("unchecked")
	private void handlePayload(CompatPayload payload, IPayloadContext context) {
		Registration<Object> registration = (Registration<Object>) registrationsById.get(payload.messageId());
		if (registration == null) {
			return;
		}

		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
		Object message = registration.decoder().apply(buffer);
		registration.handler().accept(message, () -> new NetworkEvent.Context(context));
	}

	@SuppressWarnings("unchecked")
	private CompatPayload toPayload(Object message) {
		Objects.requireNonNull(message, "message");
		Registration<Object> registration = (Registration<Object>) registrationsByClass.get(message.getClass());
		if (registration == null) {
			throw new IllegalArgumentException("Message type is not registered: " + message.getClass().getName());
		}

		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
		registration.encoder().accept(message, buffer);
		byte[] data = new byte[buffer.readableBytes()];
		buffer.getBytes(0, data);
		return new CompatPayload(payloadType, registration.id(), data);
	}

	private static StreamCodec<RegistryFriendlyByteBuf, CompatPayload> createPayloadCodec(
			CustomPacketPayload.Type<CompatPayload> payloadType) {
		Objects.requireNonNull(payloadType, "payloadType");

		return new StreamCodec<>() {
			@Override
			public CompatPayload decode(RegistryFriendlyByteBuf buffer) {
				int messageId = buffer.readVarInt();
				byte[] data = buffer.readByteArray();
				return new CompatPayload(payloadType, messageId, data);
			}

			@Override
			public void encode(RegistryFriendlyByteBuf buffer, CompatPayload payload) {
				buffer.writeVarInt(payload.messageId());
				buffer.writeByteArray(payload.data());
			}
		};
	}

	private record Registration<T>(int id, Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
	                               BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
	}

	private static final class CompatPayload implements CustomPacketPayload {
		private final CustomPacketPayload.Type<CompatPayload> type;
		private final int messageId;
		private final byte[] data;

		private CompatPayload(CustomPacketPayload.Type<CompatPayload> type, int messageId, byte[] data) {
			this.type = Objects.requireNonNull(type, "type");
			this.messageId = messageId;
			this.data = Objects.requireNonNull(data, "data");
		}

		private int messageId() {
			return messageId;
		}

		private byte[] data() {
			return data;
		}

		@Override
		public CustomPacketPayload.Type<CompatPayload> type() {
			return type;
		}
	}
}
