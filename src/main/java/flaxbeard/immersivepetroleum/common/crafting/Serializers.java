package flaxbeard.immersivepetroleum.common.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.api.crafting.DistillationRecipe;
import flaxbeard.immersivepetroleum.api.crafting.SulfurRecoveryRecipe;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.Reservoir;
import flaxbeard.immersivepetroleum.common.IPRegisters;
import flaxbeard.immersivepetroleum.common.crafting.serializers.CokerUnitRecipeSerializer;
import flaxbeard.immersivepetroleum.common.crafting.serializers.DistillationRecipeSerializer;
import flaxbeard.immersivepetroleum.common.crafting.serializers.ReservoirSerializer;
import flaxbeard.immersivepetroleum.common.crafting.serializers.SulfurRecoveryRecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

public class Serializers{
	public static final RegistryObject<IERecipeSerializer<DistillationRecipe>> DISTILLATION_SERIALIZER = IPRegisters.registerSerializer(
			"distillation", DistillationRecipeSerializer::new
	);

	public static final RegistryObject<IERecipeSerializer<CokerUnitRecipe>> COKER_SERIALIZER = IPRegisters.registerSerializer(
			"coker", CokerUnitRecipeSerializer::new
	);

	public static final RegistryObject<IERecipeSerializer<SulfurRecoveryRecipe>> HYDROTREATER_SERIALIZER = IPRegisters.registerSerializer(
			"hydrotreater", SulfurRecoveryRecipeSerializer::new
	);

	public static final RegistryObject<IERecipeSerializer<Reservoir>> RESERVOIR_SERIALIZER = IPRegisters.registerSerializer(
			"reservoirs", ReservoirSerializer::new
	);

	public static void forceClassLoad(){
	}
}
