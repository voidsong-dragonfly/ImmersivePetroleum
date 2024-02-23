package flaxbeard.immersivepetroleum.common.fluids;

import javax.annotation.Nonnull;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

public class CrudeOilFluid extends IPFluid{
	public static final Material MATERIAL = createMaterial(MaterialColor.COLOR_BLACK);
	
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
			super(entry, BlockBehaviour.Properties.of(MATERIAL).noCollission().strength(100.0F));
		}
	}
}
