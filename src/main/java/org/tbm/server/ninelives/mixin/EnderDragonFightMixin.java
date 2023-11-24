package org.tbm.server.ninelives.mixin;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EnderDragonFight.class})
public class EnderDragonFightMixin {
    @Shadow
    private boolean previouslyKilled;

    public EnderDragonFightMixin() {
    }

    @Inject(
            method = {"dragonKilled"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/boss/dragon/EnderDragonFight;generateNewEndGateway()V"
            )}
    )
    private void ninelives$respawnDragonEgg(EnderDragonEntity dragon, CallbackInfo ci) {
        this.previouslyKilled = false;
    }
}
