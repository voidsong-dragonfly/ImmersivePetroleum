package flaxbeard.immersivepetroleum.common.util.survey;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface ISurveyInfo{
	/** World-X */
	int getX();
	
	/** World-Z */
	int getZ();
	
	CompoundTag writeToStack(ItemStack stack);
	CompoundTag writeToTag(CompoundTag tag);
	
	@Nullable
	static ISurveyInfo from(ItemStack stack){
		if(stack.hasTag()){
			if(stack.getTag().contains(IslandInfo.TAG_KEY, Tag.TAG_COMPOUND))
				return new IslandInfo(stack.getTagElement(IslandInfo.TAG_KEY));
			
			if(stack.getTag().contains(SurveyScan.TAG_KEY, Tag.TAG_COMPOUND))
				return new SurveyScan(stack.getTagElement(SurveyScan.TAG_KEY));
			
		}
		
		return null;
	}
}
