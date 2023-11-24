package org.tbm.server.ninelives.mixin;

import org.tbm.server.ninelives.event.PlayerDeathCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerPlayerEntity.class})
public class ServerPlayerEntityMixin {
    @Inject(
            at = {@At("TAIL")},
            method = {"onDeath"}
    )
    private void onPlayerDeath(DamageSource source, CallbackInfo info) {
        ServerPlayerEntity player = ((ServerPlayerEntity)(Object)this);
        PlayerDeathCallback.EVENT.invoker().kill(player, source);
    }
}
