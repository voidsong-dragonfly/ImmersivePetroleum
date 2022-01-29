package flaxbeard.immersivepetroleum.common.gui;

import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.multiblocks.DerrickMultiblock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class DerrickContainer extends MultiblockAwareGuiContainer<DerrickTileEntity>{
	public DerrickContainer(int id, PlayerInventory playerInventory, DerrickTileEntity tile){
		super(tile, id, DerrickMultiblock.INSTANCE);
		
		this.addSlot(new Slot(this.inv, 0, 92, 55){
			@Override
			public boolean isItemValid(ItemStack stack){
				return ExternalModContent.isIEPipeItem(stack);
			}
		});
		
		slotCount = 1;
		
		// Player Inventory
		for(int i = 0;i < 3;i++){
			for(int j = 0;j < 9;j++){
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 20 + j * 18, 82 + i * 18));
			}
		}
		
		// Player Hotbar
		for(int i = 0;i < 9;i++){
			addSlot(new Slot(playerInventory, i, 20 + i * 18, 140));
		}
	}
}
