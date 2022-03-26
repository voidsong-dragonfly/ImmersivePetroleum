package flaxbeard.immersivepetroleum.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.render.IPRenderTypes;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class ModelPumpjack extends IPModel{
	public static final String ID = "pumpjackarm";
	public static final ResourceLocation TEXTURE = new ResourceLocation(ImmersivePetroleum.MODID, "textures/models/pumpjack_armature.png");
	
	public ModelPart origin;
	public ModelPart swingy;
	public ModelPart connector;
	public ModelPart arm;
	public ModelPart wellConnector;
	public ModelPart wellConnector2;
	
	public float ticks = 0;
	
	public ModelPumpjack(){
		super(IPRenderTypes::getEntitySolid);
		
		//this.texWidth = 190;
		//this.texHeight = 58;
	}
	
	@Override
	public void init(){
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition rootDefinition = meshDefinition.getRoot();
		
		PartDefinition origin_Definition = rootDefinition.addOrReplaceChild("origin", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 1, 1, 1), PartPose.ZERO);
		PartDefinition arm_Definition = origin_Definition.addOrReplaceChild("arm", CubeListBuilder.create().texOffs(0, 40).addBox(-24 - 16, 0, -4, 70, 10, 8), PartPose.offset(56, 48, 24));
		arm_Definition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(30, -15, -5, 12, 30, 10), PartPose.ZERO);
		arm_Definition.addOrReplaceChild("barBack", CubeListBuilder.create().texOffs(138, 0).addBox(-35F, 3F, -11F, 4, 4, 22), PartPose.ZERO);
		PartDefinition swingy_Definition = origin_Definition.addOrReplaceChild("swingy", CubeListBuilder.create().texOffs(44, 14).addBox(-4F, -2F, -14F, 8, 10, 4), PartPose.offset(24, 30, 30));
		swingy_Definition.addOrReplaceChild("swingy2", CubeListBuilder.create().texOffs(44, 14).addBox(-4F, -2F, -2F, 8, 10, 4), PartPose.ZERO);
		swingy_Definition.addOrReplaceChild("counter", CubeListBuilder.create().texOffs(44, 0).addBox(-12F, 8F, -14F, 24, 10, 4), PartPose.ZERO);
		swingy_Definition.addOrReplaceChild("counter2", CubeListBuilder.create().texOffs(44, 0).addBox(-12F, 8F, -2F, 24, 10, 4), PartPose.ZERO);
		PartDefinition connector_Definition = origin_Definition.addOrReplaceChild("connector", CubeListBuilder.create().texOffs(108, 0).addBox(-1F, -1F, -12F, 2, 24, 2), PartPose.ZERO);
		connector_Definition.addOrReplaceChild("connector2", CubeListBuilder.create().texOffs(100, 0).addBox(-1F, -1F, 6F, 2, 24, 2), PartPose.ZERO);
		origin_Definition.addOrReplaceChild("wellConnector", CubeListBuilder.create().texOffs(108, 0).addBox(-1F, 0F, -1F, 2, 30, 2), PartPose.ZERO);
		origin_Definition.addOrReplaceChild("wellConnector2", CubeListBuilder.create().texOffs(108, 0).addBox(-1F, 0F, -1F, 2, 16, 2), PartPose.ZERO);
		
		LayerDefinition layerDefinition = LayerDefinition.create(meshDefinition, 190, 58);
		ModelPart root = layerDefinition.bakeRoot();
		
		this.origin = root.getChild("origin");
		this.arm = this.origin.getChild("arm");
		this.swingy = this.origin.getChild("swingy");
		this.connector = this.origin.getChild("connector");
		this.wellConnector = this.origin.getChild("wellConnector");
		this.wellConnector2 = this.origin.getChild("wellConnector2");
		
		ModelPartOLD origin = new ModelPartOLD(this, 0, 0);
		
		ModelPartOLD arm = new ModelPartOLD(this, 0, 40);
		arm.addBox(-24 - 16, 0, -4, 70, 10, 8);
		arm.setPos(56, 48, 24);
		origin.addChild(arm);
		
		ModelPartOLD head = new ModelPartOLD(this, 0, 0);
		head.addBox(30, -15, -5, 12, 30, 10);
		arm.addChild(head);
		
		ModelPartOLD barBack = new ModelPartOLD(this, 138, 0);
		barBack.addBox(-35F, 3F, -11F, 4, 4, 22);
		arm.addChild(barBack);
		
		ModelPartOLD swingy = new ModelPartOLD(this, 44, 14);
		swingy.addBox(-4F, -2F, -14F, 8, 10, 4);
		swingy.setPos(24, 30, 30);
		origin.addChild(swingy);
		
		ModelPartOLD swingy2 = new ModelPartOLD(this, 44, 14);
		swingy2.addBox(-4F, -2F, -2F, 8, 10, 4);
		swingy.addChild(swingy2);
		
		ModelPartOLD counter = new ModelPartOLD(this, 44, 0);
		counter.addBox(-12F, 8F, -14F, 24, 10, 4);
		swingy.addChild(counter);
		
		ModelPartOLD counter2 = new ModelPartOLD(this, 44, 0);
		counter2.addBox(-12F, 8F, -2F, 24, 10, 4);
		swingy.addChild(counter2);
		
		ModelPartOLD connector = new ModelPartOLD(this, 108, 0);
		connector.addBox(-1F, -1F, -12F, 2, 24, 2);
		origin.addChild(connector);
		
		ModelPartOLD connector2 = new ModelPartOLD(this, 100, 0);
		connector2.addBox(-1F, -1F, 6F, 2, 24, 2);
		connector.addChild(connector2);
		
		ModelPartOLD wellConnector = new ModelPartOLD(this, 108, 0);
		wellConnector.addBox(-1F, 0F, -1F, 2, 30, 2);
		
		ModelPartOLD wellConnector2 = new ModelPartOLD(this, 108, 0);
		wellConnector2.addBox(-1F, 0F, -1F, 2, 16, 2);
		
		origin.addChild(wellConnector);
		origin.addChild(wellConnector2);
	}
	
	/** Dummy; To be deleted Soon™ */
	public static final class ModelPartOLD{
		public float xRot, yRot, zRot;
		public boolean visible;
		public ModelPartOLD(Model model, int tx, int ty){}
		public void setPos(float x, float y, float z){}
		public void addBox(float x, float y, float z, int width, int height, int depth){}
		public void addChild(ModelPartOLD model){}
		public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){}
	}
	
	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
		this.arm.zRot = (float) Math.toRadians(15 * Math.sin(this.ticks / 25F));
		this.swingy.zRot = (float) (2 * (Math.PI / 4) + (this.ticks / 25F));
		
		float dist = 8.5F;
		
		float sin = (float) Math.sin(this.swingy.zRot);
		float cos = (float) Math.cos(this.swingy.zRot);
		this.connector.setPos(24 - dist * sin, 30 + dist * cos, 26);
		if(sin < 0){
			this.connector.zRot = (float) (1F * (Math.PI / 2) + Math.atan(25F / (dist * sin)));
		}else if(sin > 0){
			this.connector.zRot = (float) (3F * (Math.PI / 2) + Math.atan(25F / (dist * sin)));
		}
		
		float sin2 = (float) Math.sin(this.arm.zRot);
		float cos2 = (float) Math.cos(this.arm.zRot);
		
		float x = 24 - dist * sin;
		float y = 30 + dist * cos;
		
		float w = 33F;
		float h = 4F;
		
		float tx = 56 + w * -cos2 - h * sin2;
		float ty = 48 + w * -sin2 + h * cos2;
		
		this.connector.setPos(x, y, 26);
		this.connector.zRot = (float) (3F * (Math.PI / 2) + Math.atan2(ty - y, tx - x));
		
		this.wellConnector.setPos(88F, 16F, 24F);
		this.wellConnector2.setPos(88F, 16F, 24F);
		
		float w2 = -34F;
		float h2 = -13F;
		
		float x2 = w2 * -cos2 - h2 * sin2;
		float y2 = w2 * -sin2 + h2 * cos2;
		
		float tx2 = 32F;
		float ty2 = -32F;
		
		this.wellConnector.setPos(56 + x2, 48 + y2, 24);
		this.wellConnector2.setPos(56 + x2, 48 + y2, 24);
		
		float zRot = (float) (3F * (Math.PI / 2) + Math.atan2(ty2 - y2, tx2 - x2));
		this.wellConnector.zRot = zRot;
		this.wellConnector2.zRot = zRot;
		
		double sqrt = Math.sqrt((tx2 - x2) * (tx2 - x2) + (ty2 - y2) * (ty2 - y2));
		if(sqrt <= 16){
			this.wellConnector.visible = true;
			this.wellConnector2.visible = false;
		}else{
			this.wellConnector.visible = true;
			this.wellConnector2.visible = true;
		}
		
		this.origin.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
