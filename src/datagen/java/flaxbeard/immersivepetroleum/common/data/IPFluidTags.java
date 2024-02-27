package flaxbeard.immersivepetroleum.common.data;

import java.util.concurrent.CompletableFuture;

import blusunrize.immersiveengineering.api.IETags;
import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.api.IPTags;
import flaxbeard.immersivepetroleum.common.IPContent;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.Tags.Fluids;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class IPFluidTags extends FluidTagsProvider{
	
	public IPFluidTags(PackOutput output, CompletableFuture<Provider> lookup, ExistingFileHelper exHelper){
		super(output, lookup, ImmersivePetroleum.MODID, exHelper);
	}
	
	@Override
	protected void addTags(Provider provider){
		tag(IPTags.Fluids.crudeOil).add(IPContent.Fluids.CRUDEOIL.get());
		
		tag(IPTags.Fluids.diesel)
			.add(IPContent.Fluids.DIESEL.get())
			.add(IPContent.Fluids.DIESEL_SULFUR.get());
		tag(IPTags.Fluids.diesel_sulfur).add(IPContent.Fluids.DIESEL_SULFUR.get());
		
		tag(IPTags.Fluids.gasoline).add(IPContent.Fluids.GASOLINE.get());
		tag(IPTags.Fluids.gasoline_additives).add(IPContent.Fluids.GASOLINE_ADDITIVES.get());
		
		tag(IPTags.Fluids.lubricant).add(IPContent.Fluids.LUBRICANT.get());
		tag(IPTags.Fluids.lubricant_cracked).add(IPContent.Fluids.LUBRICANT_CRACKED.get());
		
		tag(IPTags.Fluids.naphtha).add(IPContent.Fluids.NAPHTHA.get());
		tag(IPTags.Fluids.naphtha_cracked).add(IPContent.Fluids.NAPHTHA_CRACKED.get());
		
		tag(IPTags.Fluids.benzene).add(IPContent.Fluids.BENZENE.get());
		tag(IPTags.Fluids.propylene).add(IPContent.Fluids.PROPYLENE.get());
		tag(IPTags.Fluids.ethylene).add(IPContent.Fluids.ETHYLENE.get());
		tag(IPTags.Fluids.kerosene).add(IPContent.Fluids.KEROSENE.get());
		
		tag(IPTags.Fluids.napalm).add(IPContent.Fluids.NAPALM.get());
		
		tag(IPTags.Utility.burnableInFlarestack)
			.addTag(IPTags.Fluids.lubricant)
			.addTag(IPTags.Fluids.diesel)
			.addTag(IPTags.Fluids.diesel_sulfur)
			.addTag(IPTags.Fluids.gasoline)
			.addTag(IPTags.Fluids.naphtha)
			.addTag(IPTags.Fluids.naphtha_cracked)
			.addTag(IPTags.Fluids.benzene)
			.addTag(IPTags.Fluids.propylene)
			.addTag(IPTags.Fluids.ethylene)
			.addTag(IPTags.Fluids.lubricant_cracked)
			.addTag(IPTags.Fluids.kerosene)
			.addTag(IPTags.Fluids.gasoline_additives)
			.addTag(IETags.fluidPlantoil)
			.addTag(IETags.fluidCreosote)
			.addTag(IETags.fluidEthanol);
		
		tag(IETags.drillFuel)
			.addTag(IPTags.Fluids.diesel)
			.addTag(IPTags.Fluids.kerosene)
			.addTag(IPTags.Fluids.diesel_sulfur);
		
		tag(Fluids.GASEOUS)
			.add(IPContent.Fluids.PROPYLENE.get())
			.add(IPContent.Fluids.ETHYLENE.get());
	}
}
