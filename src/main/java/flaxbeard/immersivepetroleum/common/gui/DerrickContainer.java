package flaxbeard.immersivepetroleum.common.gui;

import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.multiblocks.DerrickMultiblock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;

public class DerrickContainer extends MultiblockAwareGuiContainer<DerrickTileEntity>{
	public DerrickContainer(int id, PlayerInventory playerInventory, DerrickTileEntity tile){
		super(playerInventory, tile, id, DerrickMultiblock.INSTANCE);
		
		this.addSlot(new Slot(this.inv, 0, 164, 120));
		
		slotCount = 1;
		
		// Player Hotbar
		for(int i = 0;i < 9;i++){
			addSlot(new Slot(playerInventory, i, 20 + i * 18, 163));
		}
	}
}
