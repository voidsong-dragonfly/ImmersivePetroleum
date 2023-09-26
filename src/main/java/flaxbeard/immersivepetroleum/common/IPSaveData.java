package flaxbeard.immersivepetroleum.common;

import javax.annotation.Nonnull;

import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.LubricatedTileInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

public class IPSaveData extends SavedData{
	public static final String dataName = "ImmersivePetroleum-SaveData";
	
	private static IPSaveData INSTANCE;
	
	public IPSaveData(){
		INSTANCE = this;
	}
	
	public IPSaveData(CompoundTag nbt){
		INSTANCE = this;
		
		ListTag lubricatedList = nbt.getList("lubricated", Tag.TAG_COMPOUND);
		LubricatedHandler.lubricatedTiles.clear();
		for(int i = 0;i < lubricatedList.size();i++){
			CompoundTag tag = lubricatedList.getCompound(i);
			LubricatedTileInfo info = new LubricatedTileInfo(tag);
			LubricatedHandler.lubricatedTiles.add(info);
		}
	}
	
	@Override
	@Nonnull
	public CompoundTag save(@Nonnull CompoundTag nbt){
		ListTag lubricatedList = new ListTag();
		for(LubricatedTileInfo info:LubricatedHandler.lubricatedTiles){
			if(info != null){
				CompoundTag tag = info.writeToNBT();
				lubricatedList.add(tag);
			}
		}
		nbt.put("lubricated", lubricatedList);
		
		return nbt;
	}
	
	public static void markDirty(){
		if(INSTANCE != null){
			INSTANCE.setDirty();
		}
	}
}
