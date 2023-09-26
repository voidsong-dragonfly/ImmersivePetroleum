package flaxbeard.immersivepetroleum.common.blocks.interfaces;

import net.minecraft.core.Direction;

public interface ICanSkipGUI{
	/**
	 * Used to define locations that do not require the player to sneak to avoid the GUI
	 * @param hitFace
	 * @return true if the GUI can be skipped
	 */
	boolean skipGui(Direction hitFace);
}
