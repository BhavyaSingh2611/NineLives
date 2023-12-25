package org.tbm.server.ninelives.mixin;

import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Formatting.class})
public class FormattingMixin {
    @Inject(method = "getColorValue", at = @At("RETURN"), cancellable = true)
    private void getColorValue(CallbackInfoReturnable<Integer> cir) {
        Formatting instance = (Formatting) (Object) this;

        switch (instance) {
            case BLACK -> cir.setReturnValue(0x1D1D21);
            case DARK_BLUE -> cir.setReturnValue(0x8932B8);
            case DARK_GREEN -> cir.setReturnValue(0x5E7C16);
            case DARK_AQUA -> cir.setReturnValue(0x169C9C);
            case DARK_RED -> cir.setReturnValue(0x835432);
            case DARK_PURPLE -> cir.setReturnValue(0xC74EBD);
            case GOLD -> cir.setReturnValue(0xF9801D);
            case GRAY -> cir.setReturnValue(0x9D9D97);
            case DARK_GRAY -> cir.setReturnValue(0x474F52);
            case BLUE -> cir.setReturnValue(0x3C44AA);
            case GREEN -> cir.setReturnValue(0x80C71F);
            case AQUA -> cir.setReturnValue(0x3AB3DA);
            case RED -> cir.setReturnValue(0xB02E26);
            case LIGHT_PURPLE -> cir.setReturnValue(0xF38BAA);
            case YELLOW -> cir.setReturnValue(0xFED83D);
            case WHITE -> cir.setReturnValue(0xF9FFFE);
        }
    }
}
