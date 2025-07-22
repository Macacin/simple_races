package net.simpleraces.heat;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.simpleraces.network.SimpleracesModVariables;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class HeatProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final IHeat instance = new Heat();

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == SimpleracesModVariables.HEAT ? LazyOptional.of(() -> instance).cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("heat", instance.getHeat());
        tag.putBoolean("overheated", instance.isOverheated());
        tag.putInt("overheat_ticks", instance.getOverheatTicks());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        instance.setHeat(tag.getInt("heat"));
        instance.setOverheated(tag.getBoolean("overheated"));
        instance.setOverheatTicks(tag.getInt("overheat_ticks"));
    }
}