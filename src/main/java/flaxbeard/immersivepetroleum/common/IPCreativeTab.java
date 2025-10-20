package flaxbeard.immersivepetroleum.common;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegistryObject;

public class IPCreativeTab{
	//@formatter:off
	public static final RegistryObject<CreativeModeTab> MAIN_TAB = IPRegisters.registerCreativeTab(ImmersivePetroleum.MODID, () -> CreativeModeTab.builder()
		.icon(() -> new ItemStack(IPContent.Fluids.CRUDEOIL.bucket().get()))
		.title(Component.translatable("itemGroup." + ImmersivePetroleum.MODID))
		.displayItems(IPCreativeTab::fill).build()
	);
	//@formatter:on
	
	private static void fill(CreativeModeTab.ItemDisplayParameters parms, CreativeModeTab.Output out){
		for(RegistryObject<Item> holder: IPRegisters.ITEM_REGISTER.getEntries()){
			//if(item instanceof IMightShowUpInCreativeTab i && i.addSelfToCreativeTab()){
			out.accept(holder.get());
			//}
		}
	}
	
	public static void forceClassLoad(){
	}
}
