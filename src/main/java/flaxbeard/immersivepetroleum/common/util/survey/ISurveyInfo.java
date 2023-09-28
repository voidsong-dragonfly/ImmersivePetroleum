package flaxbeard.immersivepetroleum.common.util.survey;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public interface ISurveyInfo{
	/** World-X */
	public int getX();
	
	/** World-Z */
	public int getZ();
	
	public CompoundTag writeToStack(ItemStack stack);
	public CompoundTag writeToTag(CompoundTag tag);
	
	@Nullable
	public static ISurveyInfo from(ItemStack stack){
		if(stack.hasTag()){
			if(stack.getTag().contains(IslandInfo.TAG_KEY, Tag.TAG_COMPOUND)){
				return new IslandInfo(stack.getTagElement(IslandInfo.TAG_KEY));
			}
			
			if(stack.getTag().contains(SurveyScan.TAG_KEY, Tag.TAG_COMPOUND)){
				return new SurveyScan(stack.getTagElement(SurveyScan.TAG_KEY));
			}
		}
		
		return null;
	}
}
