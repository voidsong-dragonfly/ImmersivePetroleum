package flaxbeard.immersivepetroleum.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import blusunrize.immersiveengineering.common.gui.GuiHandler;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.CokerUnitTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DerrickTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.DistillationTowerTileEntity;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.HydrotreaterTileEntity;
import flaxbeard.immersivepetroleum.common.gui.CokerUnitContainer;
import flaxbeard.immersivepetroleum.common.gui.DerrickContainer;
import flaxbeard.immersivepetroleum.common.gui.DistillationTowerContainer;
import flaxbeard.immersivepetroleum.common.gui.HydrotreaterContainer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

public class CommonProxy{
	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(ImmersivePetroleum.MODID + "/CommonProxy");
	
	/** Fired at {@link FMLCommonSetupEvent} */
	public void setup(){
	}
	
	public void registerContainersAndScreens(){
		GuiHandler.register(DistillationTowerTileEntity.class, new ResourceLocation(ImmersivePetroleum.MODID, "distillationtower"), DistillationTowerContainer::new);
		GuiHandler.register(CokerUnitTileEntity.class, new ResourceLocation(ImmersivePetroleum.MODID, "cokerunit"), CokerUnitContainer::new);
		GuiHandler.register(DerrickTileEntity.class, new ResourceLocation(ImmersivePetroleum.MODID, "derrick"), DerrickContainer::new);
		GuiHandler.register(HydrotreaterTileEntity.class, new ResourceLocation(ImmersivePetroleum.MODID, "hydrotreater"), HydrotreaterContainer::new);
	}
	
	public void preInit(){
	}
	
	public void preInitEnd(){
	}
	
	public void init(){
	}
	
	public void postInit(){
	}
	
	/** Fired at {@link FMLLoadCompleteEvent} */
	public void completed(){
	}
	
	public void serverAboutToStart(){
	}
	
	public void serverStarting(){
	}
	
	public void serverStarted(){
	}
	
	public void renderTile(BlockEntity te, VertexConsumer iVertexBuilder, PoseStack transform, MultiBufferSource buffer){
	}
	
	public void handleEntitySound(SoundEvent soundEvent, Entity entity, boolean active, float volume, float pitch){
	}
	
	public void handleTileSound(SoundEvent soundEvent, BlockEntity te, boolean active, float volume, float pitch){
	}
	
	public void drawUpperHalfSlab(PoseStack transform, ItemStack stack){
	}
	
	public void openProjectorGui(InteractionHand hand, ItemStack held){
	}
	
	public Level getClientWorld(){
		return null;
	}
	
	public Player getClientPlayer(){
		return null;
	}
	
	protected static ResourceLocation modLoc(String str){
		return new ResourceLocation(ImmersivePetroleum.MODID, str);
	}
}
