// 
// Decompiled by Procyon v0.5.36
// 

package dev.cuican.staypro.event.events.network;

import dev.cuican.staypro.event.StayEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;


@Cancelable
public class JesusEvent extends StayEvent
{
    private BlockPos pos;
    private AxisAlignedBB boundingBox;
    
    public JesusEvent(final int stage, final BlockPos pos) {
        super(stage);
        this.pos = pos;
    }
    
    public void setBoundingBox(final AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
    
    public void setPos(final BlockPos pos) {
        this.pos = pos;
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }
}
