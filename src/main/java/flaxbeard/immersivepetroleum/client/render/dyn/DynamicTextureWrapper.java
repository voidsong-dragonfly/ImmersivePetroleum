package flaxbeard.immersivepetroleum.client.render.dyn;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.platform.NativeImage;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.MCUtil;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * @author TwistedGate
 */
public class DynamicTextureWrapper{
	public static final Cache<UUID, DynamicTextureWrapper> DYN_TEXTURE_CACHE = CacheBuilder.newBuilder()
			.removalListener((s) -> {
				DynamicTextureWrapper wrapper = ((DynamicTextureWrapper) s.getValue());
				wrapper.dispose();
				
				ImmersivePetroleum.log.info("Disposed survey result texture {}", wrapper.rl);
			})
			.expireAfterAccess(5, TimeUnit.MINUTES)
			.maximumSize(50)
			.build();
	
	/** Returns null if no valid UUID is provided */
	public static DynamicTextureWrapper getOrCreate(int width, int height, CompoundTag dataTag){
		UUID uuid = dataTag.hasUUID("uuid") ? dataTag.getUUID("uuid") : null;
		if(uuid != null){
			DynamicTextureWrapper tex = DYN_TEXTURE_CACHE.getIfPresent(uuid);
			if(tex == null || tex.texture.getPixels() == null){
				tex = new DynamicTextureWrapper(width, height, uuid);
				DYN_TEXTURE_CACHE.invalidate(uuid);
				DYN_TEXTURE_CACHE.put(uuid, tex);
				
				byte[] mapData = dataTag.getByteArray("map");
				tex.write(mapData);
				
				ImmersivePetroleum.log.info("Created survey result texture {}", tex.rl);
			}
			return tex;
		}
		
		return null;
	}
	
	static DynamicTextureWrapper makeNew(int width, int height){
		return getOrCreate(width, height, UUID.randomUUID());
	}
	
	static DynamicTextureWrapper getOrCreate(int width, int height, @Nonnull UUID uuid){
		try{
			return DYN_TEXTURE_CACHE.get(uuid, () -> new DynamicTextureWrapper(width, height, uuid));
		}catch(ExecutionException e){
			// Fallback
			DynamicTextureWrapper tex = DYN_TEXTURE_CACHE.getIfPresent(uuid);
			if(tex == null){
				tex = new DynamicTextureWrapper(width, height, uuid);
				DYN_TEXTURE_CACHE.put(uuid, tex);
			}
			return tex;
		}
	}
	
	public static void clearCache(){
		DYN_TEXTURE_CACHE.invalidateAll();
		DYN_TEXTURE_CACHE.cleanUp();
	}
	
	public final int width;
	public final int height;
	@Nonnull 
	public final UUID uuid;
	public final DynamicTexture texture;
	public final RenderType renderType;
	private final ResourceLocation rl;
	
	/**
	 * @param width
	 * @param height
	 * @param typeName 
	 */
	private DynamicTextureWrapper(int width, int height, @Nonnull UUID uuid){
		Objects.requireNonNull(uuid, "Non-null UUID expected.");
		
		this.width = width;
		this.height = height;
		this.uuid = uuid;
		
		this.texture = new DynamicTexture(width, height, true);
		this.rl = ResourceUtils.ip("dyntexture/" + uuid);
		
		MCUtil.getTextureManager().register(this.rl, this.texture);
		this.renderType = RenderType.text(this.rl);
	}
	
	public void write(byte[] mapData){
		if(mapData == null || mapData.length != (this.width * this.height))
			return;
		
		if(this.texture.getPixels() != null){
			NativeImage image = this.texture.getPixels();
			for(int y = 0;y < this.width;y++){
				for(int x = 0;x < this.width;x++){
					int b = ((int) mapData[(y * this.height) + x]) & 0xFF;
					
					int rgba = (b << 16) | (b << 8) | b;
					image.setPixelRGBA(x, y, 0xFF000000 | rgba);
				}
			}
			this.texture.upload();
		}
	}
	
	public boolean isDisposed(){
		return this.texture.getPixels() == null;
	}
	
	public void dispose(){
		this.texture.close();
	}
}
