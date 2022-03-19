package flaxbeard.immersivepetroleum.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.client.render.IPRenderTypes;
import net.minecraft.client.model.geom.ModelPart;
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
		/*this.origin = new ModelPart(this, 0, 0);
		
		this.arm = new ModelPart(this, 0, 40);
		this.arm.addBox(-24 - 16, 0, -4, 70, 10, 8);
		this.arm.setPos(56, 48, 24);
		this.origin.addChild(this.arm);
		
		ModelPart head = new ModelPart(this, 0, 0);
		head.addBox(30, -15, -5, 12, 30, 10);
		this.arm.addChild(head);
		
		ModelPart barBack = new ModelPart(this, 138, 0);
		barBack.addBox(-35F, 3F, -11F, 4, 4, 22);
		this.arm.addChild(barBack);
		
		this.swingy = new ModelPart(this, 44, 14);
		this.swingy.addBox(-4F, -2F, -14F, 8, 10, 4);
		this.swingy.setPos(24, 30, 30);
		this.origin.addChild(this.swingy);
		
		ModelPart swingy2 = new ModelPart(this, 44, 14);
		swingy2.addBox(-4F, -2F, -2F, 8, 10, 4);
		this.swingy.addChild(swingy2);
		
		ModelPart counter = new ModelPart(this, 44, 0);
		counter.addBox(-12F, 8F, -14F, 24, 10, 4);
		this.swingy.addChild(counter);
		
		ModelPart counter2 = new ModelPart(this, 44, 0);
		counter2.addBox(-12F, 8F, -2F, 24, 10, 4);
		this.swingy.addChild(counter2);
		
		this.connector = new ModelPart(this, 108, 0);
		this.connector.addBox(-1F, -1F, -12F, 2, 24, 2);
		this.origin.addChild(this.connector);
		
		ModelPart connector2 = new ModelPart(this, 100, 0);
		connector2.addBox(-1F, -1F, 6F, 2, 24, 2);
		this.connector.addChild(connector2);
		
		this.wellConnector = new ModelPart(this, 108, 0);
		this.wellConnector.addBox(-1F, 0F, -1F, 2, 30, 2);
		
		this.wellConnector2 = new ModelPart(this, 108, 0);
		this.wellConnector2.addBox(-1F, 0F, -1F, 2, 16, 2);
		
		this.origin.addChild(this.wellConnector);
		this.origin.addChild(this.wellConnector2);*/
	}
	
	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha){
		if (true) return;
		arm.zRot = (float) Math.toRadians(15 * Math.sin(ticks / 25D));
		swingy.zRot = (float) (2 * (Math.PI / 4) + (ticks / 25D));
		
		float dist = 8.5F;
		
		float sin = (float) Math.sin(swingy.zRot);
		float cos = (float) Math.cos(swingy.zRot);
		connector.setPos(24 - dist * sin, 30 + dist * cos, 26);
		if(sin < 0){
			connector.zRot = (float) (1F * (Math.PI / 2) + Math.atan(25F / (dist * sin)));
		}else if(sin > 0){
			connector.zRot = (float) (3F * (Math.PI / 2) + Math.atan(25F / (dist * sin)));
		}
		
		float sin2 = (float) Math.sin(arm.zRot);
		float cos2 = (float) Math.cos(arm.zRot);
		
		float x = 24 - dist * sin;
		float y = 30 + dist * cos;
		
		float w = 33F;
		float h = 4F;
		
		float tx = 56 + w * -cos2 - h * sin2;
		float ty = 48 + w * -sin2 + h * cos2;
		
		connector.setPos(x, y, 26);
		connector.zRot = (float) (3F * (Math.PI / 2) + Math.atan2(ty - y, tx - x));
		
		wellConnector.setPos(88F, 16F, 24F);
		wellConnector2.setPos(88F, 16F, 24F);
		
		float w2 = -34F;
		float h2 = -13F;
		
		float x2 = w2 * -cos2 - h2 * sin2;
		float y2 = w2 * -sin2 + h2 * cos2;
		
		float tx2 = 32F;
		float ty2 = -32F;
		wellConnector.setPos(56 + x2, 48 + y2, 24);
		wellConnector.zRot = (float) (3F * (Math.PI / 2) + Math.atan2(ty2 - y2, tx2 - x2));
		
		wellConnector2.setPos(56 + x2, 48 + y2, 24);
		wellConnector2.zRot = (float) (3F * (Math.PI / 2) + Math.atan2(ty2 - y2, tx2 - x2));
		
		if(Math.sqrt((tx2 - x2) * (tx2 - x2) + (ty2 - y2) * (ty2 - y2)) <= 16){
			wellConnector.visible = true;
			wellConnector2.visible = false;
		}else{
			wellConnector.visible = true;
			wellConnector2.visible = true;
		}
		
		this.origin.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}
