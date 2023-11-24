package org.tbm.server.ninelives.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.DisplayEntity;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.tbm.server.ninelives.mixinInterfaces.IDisplayEntityMixin;

@Mixin({DisplayEntity.class})
public class DisplayEntityMixin implements IDisplayEntityMixin {
    @Shadow
    @Final
    private static TrackedData<Byte> BILLBOARD;
    @Shadow
    @Final
    private static TrackedData<Vector3f> SCALE;

    public TrackedData<Byte> getBillboardData() {
        return BILLBOARD;
    }

    public TrackedData<Vector3f> getScaleData() {
        return SCALE;
    }
}
