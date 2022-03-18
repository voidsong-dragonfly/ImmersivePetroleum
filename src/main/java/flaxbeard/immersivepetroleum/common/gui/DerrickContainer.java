package flaxbeard.immersivepetroleum.common.gui;

import flaxbeard.immersivepetroleum.common.ExternalModContent;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.multiblocks.DerrickMultiblock;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DerrickContainer extends MultiblockAwareGuiContainer<DerrickTileEntity>{
	public DerrickContainer(int id, net.minecraft.world.entity.player.Inventory playerInventory, DerrickTileEntity tile){
		super(null, tile, id, DerrickMultiblock.INSTANCE);
		// TODO MenuType
		
		this.addSlot(new Slot(this.inv, 0, 92, 55){
			@Override
			public boolean mayPlace(ItemStack stack){
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
