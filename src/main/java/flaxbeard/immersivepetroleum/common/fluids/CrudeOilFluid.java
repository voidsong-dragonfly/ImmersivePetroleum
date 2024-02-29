package flaxbeard.immersivepetroleum.common.fluids;

import javax.annotation.Nonnull;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class CrudeOilFluid extends IPFluid{
	public CrudeOilFluid(IPFluidEntry entry){
		super(entry);
	}
	
	@Override
	public int getTickDelay(@Nonnull LevelReader p_205569_1_){
		return 20;
	}
	
	@Override
	public boolean hasCustomSlowdown(){
		return true;
	}
	
	@Override
	public double getEntitySlowdown(){
		return 0.4;
	}
	
	public static class CrudeOilBlock extends IPFluidBlock{
		public CrudeOilBlock(IPFluidEntry entry, BlockBehaviour.Properties props){
			super(entry, BlockBehaviour.Properties.of()
	                .mapColor(MapColor.COLOR_BLACK)
	                .replaceable()
	                .noCollission()
	                .strength(100.0F)
	                .pushReaction(PushReaction.DESTROY)
	                .noLootTable()
	                .liquid()
	                .sound(SoundType.EMPTY));
		}
	}
}
