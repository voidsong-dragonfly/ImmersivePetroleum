package flaxbeard.immersivepetroleum.common;

import java.util.stream.Collectors;

import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler;
import flaxbeard.immersivepetroleum.api.crafting.LubricatedHandler.LubricatedTileInfo;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirIsland;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

public class IPSaveData extends WorldSavedData{
	private static IPSaveData INSTANCE;
	public static final String dataName = "ImmersivePetroleum-SaveData";
	
	public IPSaveData(){
		super(dataName);
	}
	
	@Override
	public void read(CompoundNBT nbt){
		ListNBT reservoirs = nbt.getList("reservoirs", NBT.TAG_COMPOUND);
		synchronized(ReservoirHandler.getReservoirIslandList()){
			ReservoirHandler.getReservoirIslandList().clear();
			for(int i = 0;i < reservoirs.size();i++){
				CompoundNBT dim = reservoirs.getCompound(i);
				ResourceLocation rl = new ResourceLocation(dim.getString("dimension"));
				RegistryKey<World> dimType = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, rl);
				ListNBT islands = dim.getList("islands", NBT.TAG_COMPOUND);
				ReservoirHandler.getReservoirIslandList().putAll(dimType, islands.stream().map(inbt -> ReservoirIsland.readFromNBT((CompoundNBT) inbt)).collect(Collectors.toList()));
			}
		}
		
		/*
		ListNBT oilList = nbt.getList("oilInfo", NBT.TAG_COMPOUND);
		PumpjackHandler.reservoirsCache.clear();
		for(int i = 0;i < oilList.size();i++){
			CompoundNBT tag = oilList.getCompound(i);
			DimensionChunkCoords coords = DimensionChunkCoords.readFromNBT(tag);
			if(coords != null){
				ReservoirWorldInfo info = ReservoirWorldInfo.readFromNBT(tag.getCompound("info"));
				PumpjackHandler.reservoirsCache.put(coords, info);
			}
		}
		*/
		
		ListNBT lubricatedList = nbt.getList("lubricated", NBT.TAG_COMPOUND);
		LubricatedHandler.lubricatedTiles.clear();
		for(int i = 0;i < lubricatedList.size();i++){
			CompoundNBT tag = lubricatedList.getCompound(i);
			LubricatedTileInfo info = new LubricatedTileInfo(tag);
			LubricatedHandler.lubricatedTiles.add(info);
		}
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt){
		ListNBT reservoirs = new ListNBT();
		synchronized(ReservoirHandler.getReservoirIslandList()){
			for(RegistryKey<World> dimension:ReservoirHandler.getReservoirIslandList().keySet()){
				CompoundNBT dim = new CompoundNBT();
				dim.putString("dimension", dimension.getLocation().toString());
				
				ListNBT islands = new ListNBT();
				for(ReservoirIsland island:ReservoirHandler.getReservoirIslandList().get(dimension)){
					islands.add(island.writeToNBT());
				}
				dim.put("islands", islands);
				
				reservoirs.add(dim);
			}
		}
		nbt.put("reservoirs", reservoirs);
		
		/*
		ListNBT oilList = new ListNBT();
		for(Map.Entry<DimensionChunkCoords, ReservoirWorldInfo> e:PumpjackHandler.reservoirsCache.entrySet()){
			if(e.getKey() != null && e.getValue() != null){
				CompoundNBT tag = e.getKey().writeToNBT();
				tag.put("info", e.getValue().writeToNBT());
				oilList.add(tag);
			}
		}
		nbt.put("oilInfo", oilList);
		*/
		
		ListNBT lubricatedList = new ListNBT();
		for(LubricatedTileInfo info:LubricatedHandler.lubricatedTiles){
			if(info != null){
				CompoundNBT tag = info.writeToNBT();
				lubricatedList.add(tag);
			}
		}
		nbt.put("lubricated", lubricatedList);
		
		return nbt;
	}
	
	public static void setDirty(){
		if(INSTANCE != null){
			INSTANCE.markDirty();
		}
	}
	
	public static void setInstance(IPSaveData in){
		INSTANCE = in;
	}
}
