package flaxbeard.immersivepetroleum.common.gui;

import static flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity.TANK_INPUT;

import flaxbeard.immersivepetroleum.api.crafting.CokerUnitRecipe;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.gui.IPSlot.FluidContainer.FluidFilter;
import flaxbeard.immersivepetroleum.common.multiblocks.CokerUnitMultiblock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import javax.annotation.Nonnull;

public class CokerUnitContainer extends MultiblockAwareGuiContainer<CokerUnitTileEntity>{
	public CokerUnitContainer(MenuType<?> type, int id, Inventory playerInventory, final CokerUnitTileEntity tile){
		super(type, tile, id, CokerUnitMultiblock.INSTANCE);
		
		addSlot(new IPSlot.CokerInput(this, this.inv, CokerUnitTileEntity.Inventory.INPUT.id(), 20, 71));
		addSlot(new IPSlot(this.inv, CokerUnitTileEntity.Inventory.INPUT_FILLED.id(), 9, 14){
			@Override
			public boolean mayPlace(@Nonnull ItemStack stack){
				return FluidUtil.getFluidHandler(stack).map(h -> {
					if(h.getTanks() <= 0 || h.getFluidInTank(0).isEmpty()){
						return false;
					}
					
					FluidStack fs = h.getFluidInTank(0);
					if(fs.isEmpty() || (tile.bufferTanks[TANK_INPUT].getFluidAmount() > 0 && !fs.isFluidEqual(tile.bufferTanks[TANK_INPUT].getFluid()))){
						return false;
					}
					
					return CokerUnitRecipe.hasRecipeWithInput(fs, true);
				}).orElse(false);
			}
		});
		addSlot(new IPSlot.ItemOutput(this.inv, CokerUnitTileEntity.Inventory.INPUT_EMPTY.id(), 9, 45));
		
		addSlot(new IPSlot.FluidContainer(this.inv, CokerUnitTileEntity.Inventory.OUTPUT_EMPTY.id(), 175, 14, FluidFilter.EMPTY));
		addSlot(new IPSlot.ItemOutput(this.inv, CokerUnitTileEntity.Inventory.OUTPUT_FILLED.id(), 175, 45));
		
		this.slotCount = CokerUnitTileEntity.Inventory.values().length;
		
		// Player Inventory
		for(int i = 0;i < 3;i++){
			for(int j = 0;j < 9;j++){
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 20 + j * 18, 105 + i * 18));
			}
		}
		
		// Hotbar
		for(int i = 0;i < 9;i++){
			addSlot(new Slot(playerInventory, i, 20 + i * 18, 163));
		}
	}
}
