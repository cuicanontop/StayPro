package dev.cuican.staypro.mixin.accessor;


import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Timer.class)
public interface AccessorTimer {



    @Accessor("tickLength")
    float aqGetTickLength();

    @Accessor("tickLength")
    void aqSetTickLength(float value);
}
