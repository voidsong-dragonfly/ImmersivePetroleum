package flaxbeard.immersivepetroleum.common.util.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandUtils{
	static void sendHelp(CommandSource source, String subIdent){
		sendTranslated(source, "chat.immersivepetroleum.command.reservoir" + subIdent + ".help");
	}
	
	static void sendString(CommandSource source, String str){
		source.sendFeedback(new StringTextComponent(str), true);
	}
	
	static void sendTranslated(CommandSource source, String translationKey, Object... args){
		source.sendFeedback(new TranslationTextComponent(translationKey, args), true);
	}
	
	static void sendTranslatedError(CommandSource source, String translationKey, Object... args){
		source.sendFeedback(new TranslationTextComponent(translationKey, args).mergeStyle(TextFormatting.RED), true);
	}
}
