package org.tbm.server.ninelives.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.decoration.DisplayEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.tbm.server.ninelives.mixinInterfaces.ITextDisplayEntityMixin;

@Mixin({DisplayEntity.TextDisplayEntity.class})
public class TextDisplayEntityMixin extends DisplayEntityMixin implements ITextDisplayEntityMixin {
    @Shadow
    @Final
    private static TrackedData<Text> TEXT;
    @Shadow
    @Final
    private static TrackedData<Integer> BACKGROUND;
    @Unique
    private String hologramName = "";
    @Unique
    private String hologramTextPlaceholder = "";
    @Unique
    private boolean isHologram = false;
    @Inject(
            method = {"writeCustomDataToNbt"},
            at = {@At("HEAD")}
    )
    protected void injectWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.putBoolean("fancyholograms.is_hologram", this.isHologram);
        if (this.isHologram) {
            nbt.putString("fancyholograms.hologram_name", this.hologramName);
            nbt.putString("fancyholograms.hologram_text_placeholder", this.hologramTextPlaceholder);
        }

    }

    @Inject(
            method = {"readCustomDataFromNbt"},
            at = {@At("HEAD")}
    )
    protected void injectReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        this.isHologram = nbt.getBoolean("fancyholograms.is_hologram");
        if (this.isHologram) {
            this.hologramName = nbt.getString("fancyholograms.hologram_name");
            this.hologramTextPlaceholder = nbt.getString("fancyholograms.hologram_text_placeholder");
        }

    }

    public boolean isHologram() {
        return this.isHologram;
    }

    public void setIsHologram(boolean isHologram) {
        this.isHologram = isHologram;
    }

    public String getHologramName() {
        return this.hologramName;
    }

    public void setHologramName(String hologramName) {
        this.hologramName = hologramName;
    }

    public String getHologramTextPlaceholder() {
        return this.hologramTextPlaceholder;
    }

    public void setHologramTextPlaceholder(String hologramTextPlaceholder) {
        this.hologramTextPlaceholder = hologramTextPlaceholder;
    }

    public TrackedData<Text> getTextData() {
        return TEXT;
    }

    public TrackedData<Integer> getBackgroundData() {
        return BACKGROUND;
    }
}
