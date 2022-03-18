package flaxbeard.immersivepetroleum.common.network;

import java.util.function.Supplier;

import flaxbeard.immersivepetroleum.common.entity.MotorboatEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;

public class MessageConsumeBoatFuel implements INetMessage{
	public int amount;
	
	public MessageConsumeBoatFuel(int amount){
		this.amount = amount;
	}
	
	public MessageConsumeBoatFuel(FriendlyByteBuf buf){
		this.amount = buf.readInt();
	}
	
	@Override
	public void toBytes(FriendlyByteBuf buf){
		buf.writeInt(amount);
	}
	
	@Override
	public void process(Supplier<Context> context){
		context.get().enqueueWork(() -> {
			Context con = context.get();
			
			if(con.getDirection().getReceptionSide() == LogicalSide.SERVER && con.getSender() != null){
				Entity entity = con.getSender().getVehicle();
				
				if(entity instanceof MotorboatEntity){
					MotorboatEntity boat = (MotorboatEntity) entity;
					FluidStack fluid = boat.getContainedFluid();
					
					if(fluid != null && fluid != FluidStack.EMPTY)
						fluid.setAmount(Math.max(0, fluid.getAmount() - amount));
					
					boat.setContainedFluid(fluid);
				}
			}
		});
	}
}
