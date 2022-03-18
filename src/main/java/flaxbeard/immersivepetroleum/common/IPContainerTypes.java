package flaxbeard.immersivepetroleum.common;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class IPContainerTypes{
	public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, ImmersivePetroleum.MODID);
	
	private static void register(String name){
		REGISTER.register(name, () -> {
			new MenuType(null);
			return null;
		});
	}
}
