package org.tbm.server.ninelives.mixinInterfaces;

import net.minecraft.entity.data.TrackedData;
import org.joml.Vector3f;

public interface IDisplayEntityMixin {
    TrackedData<Byte> getBillboardData();

    TrackedData<Vector3f> getScaleData();
}

