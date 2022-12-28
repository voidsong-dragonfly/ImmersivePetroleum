package flaxbeard.immersivepetroleum.common.data;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPContent;
import flaxbeard.immersivepetroleum.common.fluids.IPFluid;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.loaders.DynamicBucketModelBuilder;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class IPItemModels extends ItemModelProvider{
	public IPItemModels(DataGenerator gen, ExistingFileHelper exHelper){
		super(gen, ImmersivePetroleum.MODID, exHelper);
	}
	
	@Override
	public String getName(){
		return "Item Models";
	}
	
	@Override
	protected void registerModels(){
		String debugItem = name(IPContent.DEBUGITEM.get());
		
		getBuilder(debugItem)
			.parent(getExistingFile(mcLoc("item/generated")))
			.texture("layer0", modLoc("item/schematic"));
		
		genericItem(IPContent.Items.BITUMEN.get());
		genericItem(IPContent.Items.PETCOKE.get());
		genericItem(IPContent.Items.PETCOKEDUST.get());
		genericItem(IPContent.Items.OIL_CAN.get());
		genericItem(IPContent.Items.SPEEDBOAT.get());
		genericItem(IPContent.Items.PARAFFIN_WAX.get());
		
		genericItem(IPContent.BoatUpgrades.ICE_BREAKER.get());
		genericItem(IPContent.BoatUpgrades.REINFORCED_HULL.get());
		genericItem(IPContent.BoatUpgrades.PADDLES.get());
		genericItem(IPContent.BoatUpgrades.RUDDERS.get());
		genericItem(IPContent.BoatUpgrades.TANK.get());
		
		genericItem(IPContent.Items.SURVEYRESULT.get());
		
		generatorItem();
		autolubeItem();
		flarestackItem();
		surveyToolItem();
		
		// Multiblock items
		pumpjackItem();
		distillationtowerItem();
		cokerunitItem();
		hydrotreaterItem();
		derrickItem();
		oiltankItem();
		
		getBuilder(ImmersivePetroleum.MODID+":item/"+IPContent.Items.PROJECTOR.get().getRegistryName().getPath())
			.parent(getExistingFile(modLoc("item/mb_projector")));
		
		for(IPFluid.IPFluidEntry f:IPFluid.FLUIDS)
			createBucket(f.still().get());
	}
	
	private void flarestackItem(){
		ItemModelBuilder model = obj(IPContent.Blocks.FLARESTACK.get(), "block/obj/flarestack.obj")
				.texture("texture", modLoc("block/obj/flarestack"));
		
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, new Vector3f(0, 2, 0), new Vector3f(0, 45, 0), 0.25F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, new Vector3f(0, 2, 0), new Vector3f(0, 45, 0), 0.25F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, null, null, 0.25F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, null, null, 0.25F);
		doTransform(trans, TransformType.HEAD, new Vector3f(0, 12, 0), null, 0.75F);
		doTransform(trans, TransformType.GUI, new Vector3f(0, -3, 0), new Vector3f(30, 225, 0), 0.4F);
		doTransform(trans, TransformType.GROUND, new Vector3f(0, 3, 0), null, 0.25F);
		doTransform(trans, TransformType.FIXED, new Vector3f(0, -4, 0), null, 0.5F);
	}
	
	private void surveyToolItem(){
		ItemModelBuilder model = obj(IPContent.Blocks.SEISMIC_SURVEY.get(), "block/obj/seismic_survey_tool.obj")
				.texture("texture", modLoc("block/obj/seismic_survey_tool"));
		
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, null, null, 0.2F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, null, null, 0.2F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, new Vector3f(0, -2, 0), null, 0.2F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, new Vector3f(0, -2, 0), null, 0.2F);
		doTransform(trans, TransformType.HEAD, new Vector3f(0, 12, 0), null, 0.75F);
		doTransform(trans, TransformType.GUI, new Vector3f(0, -4, 0), new Vector3f(30, 225, 0), 0.3F);
		doTransform(trans, TransformType.GROUND, new Vector3f(0, 3, 0), null, 0.2F);
		doTransform(trans, TransformType.FIXED, new Vector3f(0, -4, 0), null, 0.3F);
	}
	
	private void generatorItem(){
		ItemModelBuilder model = obj(IPContent.Blocks.GAS_GENERATOR.get(), "block/obj/generator.obj")
				.texture("texture", modLoc("block/obj/generator"));
		
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, new Vector3f(0, 2.0F, 0), new Vector3f(0, 225, 0), 0.4F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, new Vector3f(0, 2.0F, 0), new Vector3f(0, 45, 0), 0.4F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, new Vector3f(0, 2.5F, 0), new Vector3f(75, 225, 0), 0.375F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, new Vector3f(0, 2.5F, 0), new Vector3f(75, 45, 0), 0.375F);
		doTransform(trans, TransformType.HEAD, new Vector3f(0, 13, 0), null, 0.8F);
		doTransform(trans, TransformType.GUI, null, new Vector3f(30, 225, 0), 0.625F);
		doTransform(trans, TransformType.GROUND, new Vector3f(0, 3, 0), null, 0.25F);
		doTransform(trans, TransformType.FIXED, null, null, 0.5F);
	}
	
	private void autolubeItem(){
		ItemModelBuilder model = obj(IPContent.Blocks.AUTO_LUBRICATOR.get(), "block/obj/autolubricator.obj")
			.texture("texture", modLoc("models/lubricator"));
		
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, new Vector3f(0, 2, 0), new Vector3f(0, 45, 0), 0.25F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, new Vector3f(0, 2, 0), new Vector3f(0, 45, 0), 0.25F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, null, null, 0.25F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, null, null, 0.25F);
		doTransform(trans, TransformType.HEAD, new Vector3f(0, 12, 0), null, 0.75F);
		doTransform(trans, TransformType.GUI, new Vector3f(0, -3, 0), new Vector3f(30, 225, 0), 0.4F);
		doTransform(trans, TransformType.GROUND, new Vector3f(0, 3, 0), null, 0.25F);
		doTransform(trans, TransformType.FIXED, new Vector3f(0, -4, 0), null, 0.5F);
	}
	
	private void distillationtowerItem(){
		ItemModelBuilder model = obj(IPContent.Multiblock.DISTILLATIONTOWER.get(), "multiblock/obj/distillationtower.obj")
			.texture("texture", modLoc("multiblock/distillation_tower"));
		
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, null, null, 0.03125F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, null, null, 0.03125F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, -5, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, -5, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, TransformType.HEAD, new Vector3f(1.5F, 8, 1.5F), null, 0.2F);
		doTransform(trans, TransformType.GUI, new Vector3f(-1, -6, 0), new Vector3f(30, 225, 0), 0.0625F);
		doTransform(trans, TransformType.GROUND, new Vector3f(1, 0, 1), null, 0.0625F);
		doTransform(trans, TransformType.FIXED, new Vector3f(0, -8, 0), null, 0.0625F);
	}
	
	private void pumpjackItem(){
		ItemModelBuilder model = obj(IPContent.Multiblock.PUMPJACK.get(), "item/obj/pumpjack_itemmockup.obj")
			.texture("texture_base", modLoc("multiblock/pumpjack_base"))
			.texture("texture_armature", modLoc("models/pumpjack_armature"));
		
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, new Vector3f(-1.75F, 2.5F, 1.25F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, new Vector3f(-1.75F, 2.5F, 1.75F), new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, new Vector3f(-0.75F, 0, -1.25F), new Vector3f(0, 90, 0), 0.03125F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, new Vector3f(1.0F, 0, -1.75F), new Vector3f(0, 270, 0), 0.03125F);
		doTransform(trans, TransformType.HEAD, new Vector3f(0, 8, -8), null, 0.2F);
		doTransform(trans, TransformType.GUI, new Vector3f(6, -6, 0), new Vector3f(30, 225, 0), 0.1875F);
		doTransform(trans, TransformType.GROUND, new Vector3f(-1.5F, 3, -1.5F), null, 0.0625F);
		doTransform(trans, TransformType.FIXED, new Vector3f(-1, -8, -2), null, 0.0625F);
	}
	
	private void cokerunitItem(){
		ItemModelBuilder model = obj(IPContent.Multiblock.COKERUNIT.get(), "multiblock/obj/cokerunit.obj")
				.texture("texture", modLoc("multiblock/cokerunit"));
		
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, null, new Vector3f(0, 225, 0), 0.03125F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, null, new Vector3f(0, 45, 0), 0.03125F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, new Vector3f(0, 2.5F, 0), new Vector3f(75, 225, 0), 0.03125F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, new Vector3f(0, 2.5F, 0), new Vector3f(75, 45, 0), 0.03125F);
		doTransform(trans, TransformType.HEAD, new Vector3f(0, 12, 0), null, 0.125F);
		doTransform(trans, TransformType.GUI, new Vector3f(0, -4, 0), new Vector3f(30, 225, 0), 0.0625F);
		doTransform(trans, TransformType.GROUND, new Vector3f(0, -8, 0), null, 0.03125F);
		doTransform(trans, TransformType.FIXED, new Vector3f(0, -8, 0), null, 0.0625F);
	}
	
	private void hydrotreaterItem(){
		ItemModelBuilder model = obj(IPContent.Multiblock.HYDROTREATER.get(), "multiblock/obj/hydrotreater.obj")
				.texture("texture", modLoc("multiblock/hydrotreater"));
		
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, null, new Vector3f(0, 225, 0), 0.0625F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, null, new Vector3f(0, 45, 0), 0.0625F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, new Vector3f(0, 2.5F, 0), new Vector3f(75, 225, 0), 0.0625F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, new Vector3f(0, 2.5F, 0), new Vector3f(75, 45, 0), 0.0625F);
		doTransform(trans, TransformType.HEAD, new Vector3f(0, 8, 0), null, 0.25F);
		doTransform(trans, TransformType.GUI, new Vector3f(-1, -1, 0), new Vector3f(30, 225, 0), 0.15625F);
		doTransform(trans, TransformType.GROUND, null, null, 0.125F);
		doTransform(trans, TransformType.FIXED, new Vector3f(0, -1, 0), null, 0.125F);
	}
	
	private void derrickItem(){
		ItemModelBuilder model = obj(IPContent.Multiblock.DERRICK.get(), "multiblock/obj/derrick.obj")
				.texture("texture", modLoc("multiblock/derrick"));
		
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, null, null, 0.0625F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, null, null, 0.0625F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, null, null, 0.0625F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, null, null, 0.0625F);
		doTransform(trans, TransformType.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), 0.15625F);
		doTransform(trans, TransformType.GUI, new Vector3f(0, -2, 0), new Vector3f(30, 45, 0), 0.0625F);
		doTransform(trans, TransformType.GROUND, new Vector3f(0, 1, 0), null, 0.0625F);
		doTransform(trans, TransformType.FIXED, new Vector3f(0, -8, 0), null, 0.0625F);
	}
	
	private void oiltankItem(){
		ItemModelBuilder model = obj(IPContent.Multiblock.OILTANK.get(), "multiblock/obj/oiltank.obj")
				.texture("texture", modLoc("multiblock/oiltank"));
		
		ModelBuilder<?>.TransformsBuilder trans = model.transforms();
		doTransform(trans, TransformType.FIRST_PERSON_LEFT_HAND, null, null, 0.0625F);
		doTransform(trans, TransformType.FIRST_PERSON_RIGHT_HAND, null, null, 0.0625F);
		doTransform(trans, TransformType.THIRD_PERSON_LEFT_HAND, null, null, 0.0625F);
		doTransform(trans, TransformType.THIRD_PERSON_RIGHT_HAND, null, null, 0.0625F);
		doTransform(trans, TransformType.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), 0.15625F);
		doTransform(trans, TransformType.GUI, new Vector3f(0, -2, 0), new Vector3f(30, 45, 0), 0.125F);
		doTransform(trans, TransformType.GROUND, new Vector3f(0, 1, 0), null, 0.0625F);
		doTransform(trans, TransformType.FIXED, new Vector3f(0, -1, 0), new Vector3f(0, 180, 0), 0.0625F);
	}
	
	private void doTransform(ModelBuilder<?>.TransformsBuilder transform, TransformType type, @Nullable Vector3f translation, @Nullable Vector3f rotationAngle, float scale){
		ModelBuilder<?>.TransformsBuilder.TransformVecBuilder trans = transform.transform(type);
		if(translation != null)
			trans.translation(translation.x(), translation.y(), translation.z());
		if(rotationAngle != null)
			trans.rotation(rotationAngle.x(), rotationAngle.y(), rotationAngle.z());
		trans.scale(scale);
		trans.end();
	}
	
	private ItemModelBuilder obj(ItemLike item, String model){
		return getBuilder(item.asItem().getRegistryName().toString())
				.customLoader(OBJLoaderBuilder::begin)
				.modelLocation(modLoc("models/" + model)).flipV(true).end();
	}
	
	private void genericItem(Item item){
		if(item == null){
			StackTraceElement where = new NullPointerException().getStackTrace()[1];
			IPDataGenerator.log.warn("Skipping null item. ( {} -> {} )", where.getFileName(), where.getLineNumber());
			return;
		}
		String name = name(item);
		
		getBuilder(name)
			.parent(getExistingFile(mcLoc("item/generated")))
			.texture("layer0", modLoc("item/"+name));
	}
	
	private void createBucket(Fluid f){
		withExistingParent(f.getBucket().asItem().getRegistryName().getPath(), ResourceUtils.forge("item/bucket"))
			.customLoader(DynamicBucketModelBuilder::begin)
			.fluid(f);
	}
	
	private String name(ItemLike item){
		return item.asItem().getRegistryName().getPath();
	}
}
