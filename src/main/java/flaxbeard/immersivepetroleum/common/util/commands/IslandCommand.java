package flaxbeard.immersivepetroleum.common.util.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import flaxbeard.immersivepetroleum.api.crafting.reservoir.Reservoir;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirHandler;
import flaxbeard.immersivepetroleum.api.crafting.reservoir.ReservoirIsland;
import flaxbeard.immersivepetroleum.common.IPSaveData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.server.level.ColumnPos;

public class IslandCommand{
	private IslandCommand(){
	}
	
	public static LiteralArgumentBuilder<CommandSourceStack> create(){
		LiteralArgumentBuilder<CommandSourceStack> main = Commands.literal("reservoir").executes(source -> {
			return Command.SINGLE_SUCCESS;
		}).requires(source -> source.hasPermission(4));
		
		main.then(Commands.literal("findnear").executes(source -> {
			return Command.SINGLE_SUCCESS;
		}));
		
		main.then(setters());
		
		return main;
	}
	
	static LiteralArgumentBuilder<CommandSourceStack> setters(){
		LiteralArgumentBuilder<CommandSourceStack> set = Commands.literal("set").executes(source -> {
			return Command.SINGLE_SUCCESS;
		}).requires(source -> source.hasPermission(4));
		
		set.then(Commands.literal("amount").executes(source -> {
			return Command.SINGLE_SUCCESS;
		}).then(Commands.argument("amount", LongArgumentType.longArg(0, 0xFFFFFFFFL)).executes(source -> {
			return Command.SINGLE_SUCCESS;
		})));
		
		set.then(Commands.literal("type").executes(source -> Command.SINGLE_SUCCESS).then(typesetter()));
		
		return set;
	}
	
	static RequiredArgumentBuilder<CommandSourceStack, String> typesetter(){
		RequiredArgumentBuilder<CommandSourceStack, String> nameArg = Commands.argument("name", StringArgumentType.string());
		
		nameArg.suggests((context, builder) -> SharedSuggestionProvider.suggest(Reservoir.map.values().stream().map(type -> type.name), builder)).executes(command -> {
			ColumnPos pos = new ColumnPos(command.getSource().getPlayerOrException().blockPosition());
			setReservoirType(command, pos);
			return Command.SINGLE_SUCCESS;
		}).then(Commands.argument("location", ColumnPosArgument.columnPos()).executes(command -> {
			ColumnPos pos = ColumnPosArgument.getColumnPos(command, "location");
			setReservoirType(command, pos);
			return Command.SINGLE_SUCCESS;
		}));
		
		return nameArg;
	}
	
	static void setReservoirType(CommandContext<CommandSourceStack> context, ColumnPos pos){
		CommandSourceStack sender = context.getSource();
		
		ReservoirIsland island = ReservoirHandler.getIsland(sender.getLevel(), pos);
		if(island == null){
			CommandUtils.sendString(sender, "The island you seek is in another castle!");
			return;
		}
		
		String name = context.getArgument("name", String.class);
		Reservoir reservoir = null;
		for(Reservoir res:Reservoir.map.values()){
			if(res.name.equalsIgnoreCase(name))
				reservoir = res;
		}
		
		if(reservoir == null){
			CommandUtils.sendTranslatedError(sender, "chat.immersivepetroleum.command.reservoir.set.invalidReservoir", name);
			return;
		}
		
		island.setReservoirType(reservoir);
		IPSaveData.markInstanceAsDirty();
		CommandUtils.sendTranslated(sender, "chat.immersivepetroleum.command.reservoir.set.sucess", reservoir.name);
	}
}
