package flaxbeard.immersivepetroleum.common.network;

import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import flaxbeard.immersivepetroleum.common.util.ResourceUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class MessageConsumeBoatFuel implements INetMessage{
	public static final ResourceLocation ID = ResourceUtils.ip("consumeboatfuel");
	
	public int amount;
	
	public MessageConsumeBoatFuel(int amount){
		this.amount = amount;
	}
	
	public MessageConsumeBoatFuel(FriendlyByteBuf buf){
		this.amount = buf.readInt();
	}
	
	@Override
	public void write(FriendlyByteBuf buf){
		buf.writeInt(amount);
	}
	
	@Override
	public ResourceLocation id(){
		return ID;
	}
	
	@Override
	public void process(PlayPayloadContext context){
		context.workHandler().execute(() -> {
			
			if(context.flow().getReceptionSide() == LogicalSide.SERVER){
				Entity vehicle = context.player().orElseThrow().getVehicle();
				
				if(vehicle instanceof MotorboatEntity boat){
					FluidStack fluid = boat.getContainedFluid();
					
					if(fluid != null && fluid != FluidStack.EMPTY)
						fluid.setAmount(Math.max(0, fluid.getAmount() - amount));
					
					boat.setContainedFluid(fluid);
				}
			}
			
		});
	}
}
