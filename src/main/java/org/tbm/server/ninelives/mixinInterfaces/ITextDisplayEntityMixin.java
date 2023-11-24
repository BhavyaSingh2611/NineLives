package org.tbm.server.ninelives.mixinInterfaces;



import net.minecraft.entity.data.TrackedData;
import net.minecraft.text.Text;

public interface ITextDisplayEntityMixin {
    boolean isHologram();

    void setIsHologram(boolean var1);

    String getHologramName();

    void setHologramName(String var1);

    String getHologramTextPlaceholder();

    void setHologramTextPlaceholder(String var1);

    TrackedData<Text> getTextData();

    TrackedData<Integer> getBackgroundData();
}