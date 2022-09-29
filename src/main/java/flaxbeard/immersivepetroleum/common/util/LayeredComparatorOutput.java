package flaxbeard.immersivepetroleum.common.util;

import java.util.function.IntConsumer;

import net.minecraft.util.Mth;

/**
 * (pretty much) a carbon copy of <a href=
 * "https://github.com/BluSunrize/ImmersiveEngineering/blob/1.18.2/src/main/java/blusunrize/immersiveengineering/common/util/LayeredComparatorOutput.java">https://github.com/BluSunrize/ImmersiveEngineering/blob/1.18.2/src/main/java/blusunrize/immersiveengineering/common/util/LayeredComparatorOutput.java</a>
 * All for the common goal of moving away from Non-API-Abuse.
 * 
 * @author BluSunrize / malte0811
 */
public class LayeredComparatorOutput{
	private final double maxValue;
	private final int numLayers;
	private final double layerSize;
	private final Runnable updateMaster;
	private final IntConsumer updateLayer;
	
	private double lastValue = -1;
	private int currentMasterOutput;
	private final int[] currentLayerOutputs;
	
	public LayeredComparatorOutput(double maxValue, int numLayers, Runnable updateMaster, IntConsumer updateLayer){
		this.maxValue = maxValue;
		this.numLayers = numLayers;
		this.updateMaster = updateMaster;
		this.updateLayer = updateLayer;
		this.currentMasterOutput = 0;
		this.currentLayerOutputs = new int[numLayers];
		this.layerSize = maxValue / numLayers;
	}
	
	public void update(double newValue){
		if(newValue == lastValue)
			return;
		lastValue = newValue;
		final int newMasterOutput = (int) ((15 * newValue) / maxValue);
		if(currentMasterOutput != newMasterOutput){
			currentMasterOutput = newMasterOutput;
			updateMaster.run();
		}
		for(int layer = 0;layer < numLayers;++layer){
			final double layerValue = newValue - layer * layerSize;
			final int newLayerOutput = (int) Mth.clamp((15 * layerValue) / layerSize, 0, 15);
			if(newLayerOutput != currentLayerOutputs[layer]){
				currentLayerOutputs[layer] = newLayerOutput;
				updateLayer.accept(layer);
			}
		}
	}
	
	public int getCurrentMasterOutput(){
		return currentMasterOutput;
	}
	
	public int getLayerOutput(int layer){
		return currentLayerOutputs[layer];
	}
	
	public int getLayers(){
		return this.numLayers;
	}
}
