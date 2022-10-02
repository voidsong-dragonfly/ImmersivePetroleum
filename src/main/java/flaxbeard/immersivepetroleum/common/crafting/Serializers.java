package flaxbeard.immersivepetroleum.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.api.crafting.DistillationTowerRecipe;
import flaxbeard.immersivepetroleum.api.crafting.HighPressureRefineryRecipe;
import flaxbeard.immersivepetroleum.api.reservoir.ReservoirType;
import flaxbeard.immersivepetroleum.common.IPRegisters;
import flaxbeard.immersivepetroleum.common.crafting.serializers.CokerUnitRecipeSerializer;
import flaxbeard.immersivepetroleum.common.crafting.serializers.DistillationTowerRecipeSerializer;
import flaxbeard.immersivepetroleum.common.crafting.serializers.HighPressureRefineryRecipeSerializer;
import flaxbeard.immersivepetroleum.common.crafting.serializers.ReservoirSerializer;
import net.minecraftforge.registries.RegistryObject;

public class Serializers{
	public static final RegistryObject<IERecipeSerializer<DistillationTowerRecipe>> DISTILLATION_SERIALIZER = IPRegisters.registerSerializer(
			"distillation", DistillationTowerRecipeSerializer::new
	);
	
	public static final RegistryObject<IERecipeSerializer<CokerUnitRecipe>> COKER_SERIALIZER = IPRegisters.registerSerializer(
			"coker", CokerUnitRecipeSerializer::new
	);
	
	public static final RegistryObject<IERecipeSerializer<HighPressureRefineryRecipe>> HYDROTREATER_SERIALIZER = IPRegisters.registerSerializer(
			"hydrotreater", HighPressureRefineryRecipeSerializer::new
	);
	
	public static final RegistryObject<IERecipeSerializer<ReservoirType>> RESERVOIR_SERIALIZER = IPRegisters.registerSerializer(
			"reservoirs", ReservoirSerializer::new
	);
	
	public static void forceClassLoad(){
	}
}
