package flaxbeard.immersivepetroleum.common;

import flaxbeard.immersivepetroleum.common.blocks.tileentities.AutoLubricatorTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.FlarestackTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.GasGeneratorTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.HydrotreaterTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.OilTankTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.PumpjackTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellPipeTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.WellTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public class IPTileTypes{
	// Multiblocks
	public static final RegistryObject<BlockEntityType<PumpjackTileEntity>> PUMP = IPRegisters.registerTE("pumpjack", PumpjackTileEntity::new, IPContent.Multiblock.pumpjack);
	public static final RegistryObject<BlockEntityType<DistillationTowerTileEntity>> TOWER = IPRegisters.registerTE("distillationtower", DistillationTowerTileEntity::new, IPContent.Multiblock.distillationtower);
	public static final RegistryObject<BlockEntityType<CokerUnitTileEntity>> COKER = IPRegisters.registerTE("cokerunit", CokerUnitTileEntity::new, IPContent.Multiblock.cokerunit);
	public static final RegistryObject<BlockEntityType<HydrotreaterTileEntity>> TREATER = IPRegisters.registerTE("hydrotreater", HydrotreaterTileEntity::new, IPContent.Multiblock.hydrotreater);
	public static final RegistryObject<BlockEntityType<DerrickTileEntity>> DERRICK = IPRegisters.registerTE("derrick", DerrickTileEntity::new, IPContent.Multiblock.derrick);
	public static final RegistryObject<BlockEntityType<OilTankTileEntity>> OILTANK = IPRegisters.registerTE("oiltank", OilTankTileEntity::new, IPContent.Multiblock.oiltank);
	
	// Normal Blocks
	public static final RegistryObject<BlockEntityType<GasGeneratorTileEntity>> GENERATOR = IPRegisters.registerTE("gasgenerator", GasGeneratorTileEntity::new, IPContent.Blocks.gas_generator);
	public static final RegistryObject<BlockEntityType<AutoLubricatorTileEntity>> AUTOLUBE = IPRegisters.registerTE("autolubricator", AutoLubricatorTileEntity::new, IPContent.Blocks.auto_lubricator);
	public static final RegistryObject<BlockEntityType<FlarestackTileEntity>> FLARE = IPRegisters.registerTE("flarestack", FlarestackTileEntity::new, IPContent.Blocks.flarestack);
	public static final RegistryObject<BlockEntityType<WellTileEntity>> WELL = IPRegisters.registerTE("well", WellTileEntity::new, IPContent.Blocks.well);
	public static final RegistryObject<BlockEntityType<WellPipeTileEntity>> WELL_PIPE = IPRegisters.registerTE("well_pipe", WellPipeTileEntity::new, IPContent.Blocks.wellPipe);
}
