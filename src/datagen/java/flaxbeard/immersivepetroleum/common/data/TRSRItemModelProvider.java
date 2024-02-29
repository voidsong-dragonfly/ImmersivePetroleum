package flaxbeard.immersivepetroleum.common.data;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class TRSRItemModelProvider extends ItemModelProvider{
	public TRSRItemModelProvider(PackOutput output, DataGenerator generator, ExistingFileHelper existingFileHelper){
		super(output, ImmersivePetroleum.MODID, existingFileHelper);
	}
}
