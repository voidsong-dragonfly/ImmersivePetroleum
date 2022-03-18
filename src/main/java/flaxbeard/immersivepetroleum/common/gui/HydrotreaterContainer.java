package flaxbeard.immersivepetroleum.common.gui;

import flaxbeard.immersivepetroleum.common.blocks.tileentities.HydrotreaterTileEntity;
import flaxbeard.immersivepetroleum.common.multiblocks.HydroTreaterMultiblock;
import net.minecraft.world.entity.player.Inventory;

public class HydrotreaterContainer extends MultiblockAwareGuiContainer<HydrotreaterTileEntity>{
	public HydrotreaterContainer(int id, Inventory playerInventory, final HydrotreaterTileEntity tile){
		super(null, tile, id, HydroTreaterMultiblock.INSTANCE);
		// TODO MenuType
	}
}
