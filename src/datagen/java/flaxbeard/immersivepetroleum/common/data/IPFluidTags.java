package flaxbeard.immersivepetroleum.common.data;

import blusunrize.immersiveengineering.api.IETags;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.IPTags;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class IPFluidTags extends FluidTagsProvider{
	
	public IPFluidTags(DataGenerator gen, ExistingFileHelper exHelper){
		super(gen, ImmersivePetroleum.MODID, exHelper);
	}
	
	@Override
	protected void addTags(){
		tag(IPTags.Fluids.diesel)
			.add(IPContent.Fluids.DIESEL.still().get())
			.add(IPContent.Fluids.DIESEL_SULFUR.still().get());
		
		tag(IPTags.Fluids.diesel_sulfur)
			.add(IPContent.Fluids.DIESEL_SULFUR.still().get());
		
		tag(IPTags.Fluids.gasoline)
			.add(IPContent.Fluids.GASOLINE.still().get());
		
		tag(IPTags.Fluids.lubricant)
			.add(IPContent.Fluids.LUBRICANT.still().get());
		
		tag(IPTags.Fluids.napalm)
			.add(IPContent.Fluids.NAPALM.still().get());
		
		tag(IPTags.Fluids.crudeOil)
			.add(IPContent.Fluids.CRUDEOIL.still().get());
		
		tag(IPTags.Utility.burnableInFlarestack)
			.addTag(IPTags.Fluids.lubricant)
			.addTag(IPTags.Fluids.diesel)
			.addTag(IPTags.Fluids.diesel_sulfur)
			.addTag(IPTags.Fluids.gasoline)
			.addTag(IETags.fluidPlantoil)
			.addTag(IETags.fluidCreosote)
			.addTag(IETags.fluidEthanol);
		
		tag(IETags.drillFuel)
			.addTag(IPTags.Fluids.diesel)
			.addTag(IPTags.Fluids.diesel_sulfur);
	}
}
