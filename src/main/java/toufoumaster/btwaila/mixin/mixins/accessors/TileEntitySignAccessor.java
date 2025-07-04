package toufoumaster.btwaila.mixin.mixins.accessors;

import net.minecraft.core.block.entity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(value = TileEntitySign.class, remap = false)
public interface TileEntitySignAccessor {
    @Accessor
    UUID getOwner();
}
