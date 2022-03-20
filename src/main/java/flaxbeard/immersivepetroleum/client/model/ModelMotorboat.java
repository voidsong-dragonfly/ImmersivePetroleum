package flaxbeard.immersivepetroleum.client.model;

import java.util.List;

import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelMotorboat extends ListModel<MotorboatEntity>{
	private final List<ModelPart> list = List.of();
	
	/**
	 * Part of the model rendered to make it seem like there's no water in the
	 * boat
	 */
	public ModelPart noWater;
	
	public ModelPart[] boatSides = new ModelPart[5];
	public ModelPart motor;
	public ModelPart propeller;
	public ModelPart propellerAssembly;
	
	public ModelPart icebreak;
	public ModelPart coreSampleBoat;
	public ModelPart coreSampleBoatDrill;
	public ModelPart tank;
	public ModelPart rudder1;
	public ModelPart rudder2;
	public ModelPart ruddersBase;
	public ModelPart[] paddles = new ModelPart[2];
	
	public ModelMotorboat(){
		/*
		this.boatSides[0] = (new ModelPart(this, 0, 0)).setTexSize(128, 64);
		this.boatSides[1] = (new ModelPart(this, 0, 19)).setTexSize(128, 64);
		this.boatSides[2] = (new ModelPart(this, 0, 27)).setTexSize(128, 64);
		this.boatSides[3] = (new ModelPart(this, 0, 35)).setTexSize(128, 64);
		this.boatSides[4] = (new ModelPart(this, 0, 43)).setTexSize(128, 64);
		this.boatSides[0].addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
		this.boatSides[0].setPos(0.0F, 3.0F, 1.0F);
		this.boatSides[1].addBox(-13.0F, -7.0F, -1.0F, 18, 6, 2, 0.0F);
		this.boatSides[1].setPos(-15.0F, 4.0F, 4.0F);
		this.boatSides[2].addBox(-8.0F, -7.0F, -1.0F, 16, 6, 2, 0.0F);
		this.boatSides[2].setPos(15.0F, 4.0F, 0.0F);
		this.boatSides[3].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
		this.boatSides[3].setPos(0.0F, 4.0F, -9.0F);
		this.boatSides[4].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
		this.boatSides[4].setPos(0.0F, 4.0F, 9.0F);
		this.boatSides[0].xRot = ((float) Math.PI / 2F);
		this.boatSides[1].yRot = ((float) Math.PI * 3F / 2F);
		this.boatSides[2].yRot = ((float) Math.PI / 2F);
		this.boatSides[3].yRot = (float) Math.PI;
		
		this.noWater = (new ModelPart(this, 0, 0)).setTexSize(128, 64);
		this.noWater.addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
		this.noWater.setPos(0.0F, -3.0F, 1.0F);
		this.noWater.xRot = ((float) Math.PI / 2F);
		refresh();
		
		this.paddles[0] = this.makePaddle(true);
		this.paddles[0].setPos(3.0F, -5.0F, 9.0F);
		this.paddles[1] = this.makePaddle(false);
		this.paddles[1].setPos(3.0F, -5.0F, -9.0F);
		this.paddles[1].yRot = (float) Math.PI;
		this.paddles[0].zRot = 0.19634955F;
		this.paddles[1].zRot = 0.19634955F;
		
		ImmutableList.Builder<ModelPart> builder = ImmutableList.builder();
		
		builder.addAll(Arrays.asList(this.boatSides));
		builder.addAll(Arrays.asList(
				this.motor,
				this.propellerAssembly));
		
		this.list = builder.build();*/
	}
	
	public void refresh(){
		/*motor = new ModelPart(this, 104, 0).setTexSize(128, 64);
		motor.addBox(-19.0F, -8.0F, -3, 6, 5, 6, 0.0F);
		
		propellerAssembly = new ModelPart(this, 96, 0).setTexSize(128, 64);
		propellerAssembly.setPos(-17F, 5F, 0);
		
		propellerAssembly.addBox(-1, -8.1F, -1, 2, 10, 2, 0.0F);
		
		ModelPart handle = new ModelPart(this, 72, 0).setTexSize(128, 64);
		handle.addBox(4F, -9.7F, -0.5F, 6, 1, 1);
		handle.xRot = 0;
		handle.zRot = (float) Math.toRadians(-5);
		propellerAssembly.addChild(handle);
		
		propeller = new ModelPart(this, 86, 0).setTexSize(128, 64);
		propeller.addBox(-1F, -1F, -1F, 3, 2, 2, 0.0F);
		propeller.setPos(-3F, 0, 0);
		propellerAssembly.addChild(propeller);
		
		ModelPart propeller1 = new ModelPart(this, 90, 4).setTexSize(128, 64);
		propeller1.addBox(0F, 0F, -1F, 1, 4, 2, 0.0F);
		propeller1.yRot = (float) Math.toRadians(15);
		propeller.addChild(propeller1);
		
		ModelPart propeller2B = new ModelPart(this, 90, 4).setTexSize(128, 64);
		propeller.addChild(propeller2B);
		propeller2B.xRot = (float) Math.toRadians(360F / 3F);
		
		ModelPart propeller2 = new ModelPart(this, 90, 4).setTexSize(128, 64);
		propeller2.addBox(0F, 0F, -1F, 1, 4, 2, 0.0F);
		propeller2.yRot = (float) Math.toRadians(15);
		propeller2B.addChild(propeller2);
		
		ModelPart propeller3B = new ModelPart(this, 90, 4).setTexSize(128, 64);
		propeller.addChild(propeller3B);
		propeller3B.xRot = (float) Math.toRadians(2 * 360F / 3F);
		
		ModelPart propeller3 = new ModelPart(this, 90, 4).setTexSize(128, 64);
		propeller3.addBox(0F, 0F, -1F, 1, 4, 2, 0.0F);
		propeller3.yRot = (float) Math.toRadians(15);
		propeller3B.addChild(propeller3);
//		this.boatSides[4] = (new ModelRenderer(this, 0, 43)).setTextureSize(128, 64);
//		this.boatSides[4].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
//		this.boatSides[4].setRotationPoint(0.0F, 4.0F, 9.0F);
		
		icebreak = (new ModelPart(this, 34, 56)).setTexSize(128, 64);
		icebreak.addBox(16, -2, -2, 7, 4, 4, 0.0F);
		
		tank = (new ModelPart(this, 86, 24)).setTexSize(128, 64);
		tank.addBox(-14, -2, -8, 5, 5, 16, 0.0F);
		
		ruddersBase = (new ModelPart(this, 92, 29)).setTexSize(128, 64);
		ruddersBase.addBox(-18, -3, -8, 2, 6, 3, 0.0F);
		
		ModelPart ruddersBase2 = (new ModelPart(this, 92, 29)).setTexSize(128, 64);
		ruddersBase2.addBox(-18, -3, 6, 2, 6, 3, 0.0F);
		ruddersBase.addChild(ruddersBase2);
		
		rudder1 = (new ModelPart(this, 112, 23)).setTexSize(128, 64);
		rudder1.setPos(-15, 3, -6.5f);
		rudder1.addBox(-4, 0, -.5f, 4, 6, 1, 0.0F);
		
		rudder2 = (new ModelPart(this, 112, 23)).setTexSize(128, 64);
		rudder2.setPos(-15, 3, 7.5f);
		rudder2.addBox(-4, 0, -.5f, 4, 6, 1, 0.0F);
		
		ModelPart pipe1 = (new ModelPart(this, 112, 38)).setTexSize(128, 64);
		pipe1.addBox(-13, -3, 4, 1, 1, 1, 0.0F);
		tank.addChild(pipe1);
		
		ModelPart pipe2 = (new ModelPart(this, 116, 38)).setTexSize(128, 64);
		pipe2.addBox(-15, -4, 4, 3, 1, 1, 0.0F);
		tank.addChild(pipe2);
		
		ModelPart pip3 = (new ModelPart(this, 112, 38)).setTexSize(128, 64);
		pip3.addBox(-15, -4, 3, 1, 1, 1, 0.0F);
		tank.addChild(pip3);
		
		coreSampleBoat = (new ModelPart(this, 10, 0)).setTexSize(128, 64);
		coreSampleBoat.addBox(-10, -1, -13, 4, 2, 2, 0.0F);
		
		ModelPart core2 = (new ModelPart(this, 10, 0)).setTexSize(128, 64);
		core2.addBox(-11, -2, -14, 1, 4, 4, 0.0F);
		coreSampleBoat.addChild(core2);
		
		ModelPart core3 = (new ModelPart(this, 10, 0)).setTexSize(128, 64);
		core3.addBox(-6, -2, -14, 1, 4, 4, 0.0F);
		coreSampleBoat.addChild(core3);
		
		coreSampleBoatDrill = (new ModelPart(this, 10, 0)).setTexSize(128, 64);
		coreSampleBoatDrill.addBox(-3, -8, -16, 6, 18, 6, 0.0F);
		
		ModelPart iS1 = (new ModelPart(this, 56, 52)).setTexSize(128, 64);
		iS1.addBox(0.01f, -7.01F, -0.01F, 16, 10, 2, 0.0F);
		iS1.setPos(26.0F, 3.0F, 0.0F);
		iS1.yRot = (float) Math.toRadians(180 + 45);
		icebreak.addChild(iS1);
		
		ModelPart iS1T = (new ModelPart(this, 100, 45)).setTexSize(128, 64);
		iS1T.addBox(4, 0, -2F, 12, 5, 2, 0.0F);
		iS1T.setPos(0F, -7F, 0F);
		iS1T.xRot = (float) Math.toRadians(180 - 23);
		iS1.addChild(iS1T);
		
		ModelPart iS2 = (new ModelPart(this, 56, 52)).setTexSize(128, 64);
		iS2.addBox(0, -7.0F, -2F, 16, 10, 2, 0.0F);
		iS2.setPos(26.0F, 3.0F, 0.0F);
		iS2.yRot = (float) Math.toRadians(180 - 45);
		icebreak.addChild(iS2);
		
		ModelPart iS2T = (new ModelPart(this, 100, 45)).setTexSize(128, 64);
		iS2T.addBox(4, 0, 0F, 12, 5, 2, 0.0F);
		iS2T.setPos(0F, -7F, 0F);
		iS2T.xRot = (float) Math.toRadians(180 + 23);
		iS2.addChild(iS2T);*/
	}

	/*
	ModelPart makePaddle(boolean left){
		ModelPart model = (new ModelPart(this, 62, left ? 2 : 22)).setTexSize(128, 64);
		model.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18);
		model.addBox(left ? -1.001F : 0.001F, -3.0F, 8.0F, 1, 6, 7);
		return model;
	}
	 */
	
	@Override
	public void setupAnim(MotorboatEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		MotorboatEntity boatEntity = (MotorboatEntity) entityIn;
		
		this.setPaddleRotationAngles(boatEntity, 0, limbSwing, boatEntity.isEmergency());
		this.setPaddleRotationAngles(boatEntity, 1, limbSwing, boatEntity.isEmergency());
	}
	
	public void setPaddleRotationAngles(Boat boat, int paddle, float limbSwing, boolean rowing){
		if(rowing){
			float f = boat.getRowingTime(paddle, limbSwing);
			ModelPart model = this.paddles[paddle];
			model.xRot = (float) Mth.clampedLerp((double) (-(float) Math.PI / 3F), (double) -0.2617994F, (double) ((Mth.sin(-f) + 1.0F) / 2.0F));
			model.yRot = (float) Mth.clampedLerp((double) (-(float) Math.PI / 4F), (double) ((float) Math.PI / 4F), (double) ((Mth.sin(-f + 1.0F) + 1.0F) / 2.0F));
			
			model.setPos(3.0F, -5.0F, 9.0F);
			
			if(paddle == 1){
				model.setPos(3.0F, -5.0F, -9.0F);
				model.yRot = (float) Math.PI - model.yRot;
			}
		}else{
			ModelPart model = this.paddles[paddle];
			model.xRot = (float) Math.toRadians(-25);
			model.yRot = (float) Math.toRadians(-90);
			
			model.setPos(3.0F, -2.0F, 11.0F);
			
			if(paddle == 1){
				model.setPos(3.0F, -2.0F, -11.0F);
				model.yRot = (float) Math.PI - model.yRot;
			}
		}
	}
	
	/**
	 * Only contains the base shape
	 */
	@Override
	public Iterable<ModelPart> parts(){
		return this.list;
	}
	
	public ModelPart noWaterRenderer(){
		return this.noWater;
	}
}
