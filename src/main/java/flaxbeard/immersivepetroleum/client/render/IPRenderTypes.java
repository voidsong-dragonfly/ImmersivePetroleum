package flaxbeard.immersivepetroleum.client.render;

import java.util.OptionalDouble;

import com.mojang.blaze3d.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class IPRenderTypes extends RenderStateShard{
	static final ResourceLocation activeTexture = new ResourceLocation(ImmersivePetroleum.MODID, "textures/multiblock/distillation_tower_active.png");
	static final ResourceLocation oilTankTexture = new ResourceLocation(ImmersivePetroleum.MODID, "textures/multiblock/oiltank.png");
	
	/**
	 * Intended to only be used by {@link MultiblockDistillationTowerRenderer}
	 */
	public static final RenderType DISTILLATION_TOWER_ACTIVE;
	public static final RenderType OIL_TANK;
	public static final RenderType TRANSLUCENT_LINES;
	public static final RenderType TRANSLUCENT_POSITION_COLOR;
	public static final RenderType ISLAND_DEBUGGING_POSITION_COLOR;

	static final RenderStateShard.TextureStateShard TEXTURE_ACTIVE_TOWER = new RenderStateShard.TextureStateShard(activeTexture, false, false);
	static final RenderStateShard.TextureStateShard TEXTURE_OIL_TANK = new RenderStateShard.TextureStateShard(oilTankTexture, false, false);
	static final RenderStateShard.LightmapStateShard LIGHTMAP_ENABLED = new RenderStateShard.LightmapStateShard(true);
	static final RenderStateShard.OverlayStateShard OVERLAY_ENABLED = new RenderStateShard.OverlayStateShard(true);
	static final RenderStateShard.OverlayStateShard OVERLAY_DISABLED = new RenderStateShard.OverlayStateShard(false);
	static final RenderStateShard.DepthTestStateShard DEPTH_ALWAYS = new RenderStateShard.DepthTestStateShard("always", GL11.GL_ALWAYS);
	static final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
	}, RenderSystem::disableBlend);
	static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", () -> {
		RenderSystem.disableBlend();
	}, () -> {
	});

	static{
		// TODO fix. Lines are weird in 1.17+
		TRANSLUCENT_LINES = RenderType.create(
				ImmersivePetroleum.MODID+":translucent_lines",
				DefaultVertexFormat.POSITION_COLOR,
				VertexFormat.Mode.LINES,
				256,
				false,
				false,
				RenderType.CompositeState.builder().setTransparencyState(TRANSLUCENT_TRANSPARENCY)
					.setLineState(new LineStateShard(OptionalDouble.of(3.5)))
					.setDepthTestState(DEPTH_ALWAYS)
					.createCompositeState(false)
		);

		DISTILLATION_TOWER_ACTIVE = RenderType.create(
				ImmersivePetroleum.MODID+":distillation_tower_active",
				DefaultVertexFormat.BLOCK,
				VertexFormat.Mode.QUADS,
				256,
				true,
				false,
				RenderType.CompositeState.builder()
					.setTextureState(TEXTURE_ACTIVE_TOWER)
					.setLightmapState(LIGHTMAP_ENABLED)
					.setOverlayState(OVERLAY_DISABLED)
					.createCompositeState(false)
		);
		
		OIL_TANK = RenderType.create(
				ImmersivePetroleum.MODID+":oil_tank",
				DefaultVertexFormat.BLOCK,
				VertexFormat.Mode.QUADS,
				256,
				true,
				false,
				RenderType.CompositeState.builder()
					.setTextureState(TEXTURE_OIL_TANK)
					.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
					.setLightmapState(LIGHTMAP_ENABLED)
					.setOverlayState(OVERLAY_DISABLED)
					.createCompositeState(false)
		);
		
		TRANSLUCENT_POSITION_COLOR = RenderType.create(
				ImmersivePetroleum.MODID+":translucent_pos_color",
				DefaultVertexFormat.POSITION_COLOR,
				VertexFormat.Mode.QUADS,
				256,
				false,
				false,
				RenderType.CompositeState.builder()
					.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
					.createCompositeState(false)
		);
		
		ISLAND_DEBUGGING_POSITION_COLOR = RenderType.create(
				ImmersivePetroleum.MODID+":translucent_pos_color",
				DefaultVertexFormat.POSITION_COLOR,
				VertexFormat.Mode.QUADS,
				256,
				false,
				false,
				RenderType.CompositeState.builder()
					.setCullState(new CullStateShard(false))
					.createCompositeState(false)
		);
	}

	private IPRenderTypes(String pName, Runnable pSetupState, Runnable pClearState){
		super(pName, pSetupState, pClearState);
		throw new UnsupportedOperationException();
	}

	/** Same as vanilla, just without an overlay */
	public static RenderType getEntitySolid(ResourceLocation locationIn){
		RenderType.CompositeState renderState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false))
				.setTransparencyState(NO_TRANSPARENCY)
				.setLightmapState(LIGHTMAP_ENABLED)
				.setOverlayState(OVERLAY_DISABLED)
				.createCompositeState(true);
		return RenderType.create("entity_solid", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, renderState);
	}

	// TODO this is very very broken in 1.17+
	public static MultiBufferSource disableLighting(MultiBufferSource in){
		return type -> {
			@SuppressWarnings("deprecation")
			RenderType rt = new RenderType(
					ImmersivePetroleum.MODID + ":" + type + "_no_lighting",
					type.format(),
					type.mode(),
					type.bufferSize(),
					type.affectsCrumbling(),
					false,
					() -> {
						type.setupRenderState();
						
						//RenderSystem.disableLighting();
					}, () -> {
						type.clearRenderState();
					}){};
			return in.getBuffer(rt);
		};
	}
}
