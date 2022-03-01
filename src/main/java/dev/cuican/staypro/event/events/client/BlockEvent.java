package dev.cuican.staypro.event.events.client;


import dev.cuican.staypro.event.StayEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class BlockEvent
        extends StayEvent {
    public BlockPos pos;
    public EnumFacing facing;
    public BlockEvent(int stage, BlockPos pos, EnumFacing facing) {
        super(stage);
        this.pos = pos;
        this.facing = facing;
    }
}

