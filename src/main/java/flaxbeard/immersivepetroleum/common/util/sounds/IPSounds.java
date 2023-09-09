package flaxbeard.immersivepetroleum.common.util.sounds;

import flaxbeard.immersivepetroleum.common.IPRegisters;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public class IPSounds{
	public final static RegistryObject<SoundEvent> FLARESTACK = IPRegisters.registerSoundEvent("flarestack_fire");
	public final static RegistryObject<SoundEvent> PROJECTOR = IPRegisters.registerSoundEvent("projector");
}
