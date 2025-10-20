package flaxbeard.immersivepetroleum.common;

import blusunrize.immersiveengineering.common.register.IEMenuTypes;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.CokerUnitLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.DerrickLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.distillation_tower.DistillationTowerLogic;
import flaxbeard.immersivepetroleum.common.blocks.multiblocks.logic.hydro_treater.HydroTreaterLogic;
import flaxbeard.immersivepetroleum.common.gui.CokerUnitContainer;
import flaxbeard.immersivepetroleum.common.gui.DerrickContainer;
import flaxbeard.immersivepetroleum.common.gui.DistillationTowerContainer;
import flaxbeard.immersivepetroleum.common.gui.HydrotreaterContainer;

public class IPMenuTypes{
	public static void forceClassLoad(){
	}
	
	public static final IEMenuTypes.MultiblockContainer<DistillationTowerLogic.State, DistillationTowerContainer> DISTILLATION_TOWER;
	public static final IEMenuTypes.MultiblockContainer<DerrickLogic.State, DerrickContainer> DERRICK;
	public static final IEMenuTypes.MultiblockContainer<CokerUnitLogic.State, CokerUnitContainer> COKER;
	public static final IEMenuTypes.MultiblockContainer<HydroTreaterLogic.State, HydrotreaterContainer> HYDROTREATER;
	
	static{
		DISTILLATION_TOWER = IEMenuTypes.registerMultiblock(
			"distillation_tower",
			DistillationTowerContainer::makeServer,
			DistillationTowerContainer::makeClient
		);
		
		DERRICK = IEMenuTypes.registerMultiblock(
			"derrick",
			DerrickContainer::makeServer,
			DerrickContainer::makeClient
		);
		
		COKER = IEMenuTypes.registerMultiblock(
			"coker",
			CokerUnitContainer::makeServer,
			CokerUnitContainer::makeClient
		);
		
		HYDROTREATER = IEMenuTypes.registerMultiblock(
			"hydrotreater",
			HydrotreaterContainer::makeServer,
			HydrotreaterContainer::makeClient
		);
	}
}
