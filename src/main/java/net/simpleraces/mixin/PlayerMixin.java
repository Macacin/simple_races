package net.simpleraces.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.simpleraces.network.SimpleracesModVariables;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    public PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean onClimbable() {
        var vars = ((Player)(Object) this).getCapability(SimpleracesModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElse(new SimpleracesModVariables.PlayerVariables());
        if (this.horizontalCollision && this.isAlive() && vars.aracha) {
            return true;
        }
        return super.onClimbable();
    }
}