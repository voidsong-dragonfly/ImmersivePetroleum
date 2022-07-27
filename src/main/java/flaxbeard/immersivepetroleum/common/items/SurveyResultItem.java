package flaxbeard.immersivepetroleum.common.items;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * TODO Highly Experimental.
 * 
 * @author TwistedGate
 */
public class SurveyResultItem extends IPItemBase{
	public SurveyResultItem(){
		super(new Item.Properties().stacksTo(1));
	}
	
	@Override
	public Component getName(ItemStack stack){
		String selfKey = getDescriptionId(stack);
		if(stack.hasTag() && stack.getTag() != null){
			if(stack.getTag().contains("surveyscan") || stack.getTag().contains("islandscan")){
				CompoundTag tag;
				if((tag = stack.getTagElement("surveyscan")) == null){
					tag = stack.getTagElement("islandscan");
				}
				
				if(tag != null){
					int x = tag.getInt("x");
					int z = tag.getInt("z");
					
					return new TranslatableComponent(selfKey).append(new TextComponent(String.format(Locale.ENGLISH, " [%d, %d]", x, z))).withStyle(ChatFormatting.GOLD);
				}
			}
		}
		
		return new TranslatableComponent(selfKey).withStyle(ChatFormatting.GOLD);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		if(stack.hasTag() && stack.getTag() != null){
			if(stack.getTag().contains("surveyscan") ){
				CompoundTag tag = stack.getTagElement("surveyscan");
				
				tooltip.add(new TextComponent("Hold in Hand."));
				
				if(flagIn == TooltipFlag.Default.ADVANCED){
					UUID uuid = tag.hasUUID("uuid") ? tag.getUUID("uuid") : null;
					byte[] mapData = tag.getByteArray("map");
					
					tooltip.add(new TextComponent("ID: " + (uuid != null ? uuid.toString() : "Null")));
					tooltip.add(new TextComponent("dSize: " + (mapData != null ? mapData.length : "Null")));
				}
			}
			
			if(stack.getTag().contains("islandscan")){
				CompoundTag tag = stack.getTagElement("islandscan");
				long amount = tag.getLong("amount");
				byte percentage = tag.getByte("status");
				String fluidTranslation = tag.getString("fluid");
				
				tooltip.add(new TranslatableComponent(fluidTranslation).withStyle(ChatFormatting.DARK_GRAY));
				tooltip.add(new TranslatableComponent("desc.immersivepetroleum.info.survey_result.amount", String.format(Locale.ENGLISH, "%,.3f", amount / 1000D), percentage).withStyle(ChatFormatting.DARK_GRAY));
			}
		}
	}
}
