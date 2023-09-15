package flaxbeard.immersivepetroleum.common.entity;

import flaxbeard.immersivepetroleum.common.IPRegisters;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;

public class IPEntityTypes{
	public static final RegistryObject<EntityType<MotorboatEntity>> MOTORBOAT = IPRegisters.registerEntityType("speedboat", s -> {
		EntityType<MotorboatEntity> ret = EntityType.Builder.<MotorboatEntity> of(MotorboatEntity::new, MobCategory.MISC)
			.sized(1.375F, 0.5625F)
			.clientTrackingRange(10)
			.build(s.toString());
		return ret;
	});
	

	public static final RegistryObject<EntityType<MolotovItemEntity>> MOLOTOV = createType();
	
	private static RegistryObject<EntityType<MolotovItemEntity>> createType(){
		
		return IPRegisters.registerEntityType("molotov", s -> {
			EntityType<MolotovItemEntity> ret = EntityType.Builder.<MolotovItemEntity> of(MolotovItemEntity::new, MobCategory.MISC)
					.sized(0.25F, 0.25F)
					.clientTrackingRange(4)
					.updateInterval(10)
					.build(s.toString());
			return ret;
		});
	}
	
	public static void forceClassLoad(){
	}
}
