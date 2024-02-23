package flaxbeard.immersivepetroleum.common;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Row;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = ImmersivePetroleum.MODID, bus = Bus.MOD)
public class IPCreativeTab{
	
	public static final Holder<CreativeModeTab> TAB = IPRegisters.registerCreativeTab(ImmersivePetroleum.MODID, () -> {
		//@formatter:off
		return new CreativeModeTab.Builder(Row.TOP, 0)
				.icon(() -> IPContent.Fluids.CRUDEOIL.bucket().get().getDefaultInstance())
				.title(Component.literal(ImmersivePetroleum.MODID))
				.displayItems(IPCreativeTab::fill)
				.build();
		//@formatter:on
	});
	
	private static void fill(CreativeModeTab.ItemDisplayParameters parms, CreativeModeTab.Output out){
		for(Holder<Item> holder:IPRegisters.ITEM_REGISTER.getEntries()){
			Item item = holder.value();
			
			if(item instanceof IMightShowUpInCreativeTab i && i.addSelfToCreativeTab()){
				out.accept(item);
			}
		}
	}
	
	@SubscribeEvent
	public static void fillTab(BuildCreativeModeTabContentsEvent ev){
		// Just incase
	}
	
	/**
	 * I find it amusing doing it this way
	 * 
	 * @author TwistedGate
	 */
	public interface IMightShowUpInCreativeTab{
		default boolean addSelfToCreativeTab(){
			return true;
		}
	}
}
