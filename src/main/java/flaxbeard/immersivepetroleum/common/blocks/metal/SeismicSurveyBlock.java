package flaxbeard.immersivepetroleum.common.blocks.metal;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import flaxbeard.immersivepetroleum.ImmersivePetroleum;
import flaxbeard.immersivepetroleum.common.IPTileTypes;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockBase;
import flaxbeard.immersivepetroleum.common.blocks.IPBlockItemBase;
import flaxbeard.immersivepetroleum.common.blocks.tileentities.SeismicSurveyTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * TODO Highly Experimental. Name not final. Function not final.
 * 
 * @author TwistedGate
 */
public class SeismicSurveyBlock extends IPBlockBase implements EntityBlock{
	
	public static final BooleanProperty SLAVE = BooleanProperty.create("slave");
	
	public SeismicSurveyBlock(){
		super(Block.Properties.of(Material.METAL).strength(5.0F, 6.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());
		
		registerDefaultState(getStateDefinition().any()
				.setValue(SLAVE, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder){
		builder.add(SLAVE);
	}
	
	@Override
	public Supplier<BlockItem> blockItemSupplier(){
		return () -> new SeismicSurveyBlockItem(this);
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState){
		SeismicSurveyTileEntity te = IPTileTypes.SEISMIC_SURVEY.get().create(pPos, pState);
		te.isSlave = pState.getValue(SLAVE);
		return te;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type){
		return createTickerHelper(level.isClientSide, type, IPTileTypes.SEISMIC_SURVEY);
	}
	
	@Override
	public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player){
		if(state.getValue(SLAVE)){
			worldIn.destroyBlock(pos.below(), !player.isCreative());
		}else{
			worldIn.destroyBlock(pos.above(), false);
		}
		
		super.playerWillDestroy(worldIn, pos, state, player);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder){
		if(state.getValue(SLAVE)){
			// TODO Don't know how else i would do this yet
			return Collections.emptyList();
		}
		
		return super.getDrops(state, builder);
	}
	
	@Override
	public void destroy(LevelAccessor world, BlockPos pos, BlockState state){
		BlockEntity te = world.getBlockEntity(pos);
		if(!((Level) world).isClientSide && te instanceof SeismicSurveyTileEntity survey && !survey.isSlave){
			if(!survey.stack.isEmpty()){
				Block.popResource((Level) world, pos, survey.stack);
			}
		}
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof SeismicSurveyTileEntity survey){
			if(survey.interact(state, world, pos, player, hand)){
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(!worldIn.isClientSide){
			worldIn.setBlockAndUpdate(pos.offset(0, 1, 0), state.setValue(SLAVE, true));
		}
	}
	
	static final VoxelShape SHAPE_SLAVE = Shapes.box(0, 0, 0, 1, 1, 1);
	static final VoxelShape SHAPE_MASTER = Shapes.box(0, 0, 0, 1, 1, 1);
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return state.getValue(SLAVE) ? SHAPE_SLAVE : SHAPE_MASTER;
	}
	
	public static class SeismicSurveyBlockItem extends IPBlockItemBase{
		public SeismicSurveyBlockItem(Block blockIn){
			super(blockIn, new Item.Properties().tab(ImmersivePetroleum.creativeTab));
		}
		
		@Override
		protected boolean canPlace(BlockPlaceContext con, BlockState state){
			if(super.canPlace(con, state)){
				BlockPos otherPos = con.getClickedPos().relative(Direction.UP);
				BlockState otherState = con.getLevel().getBlockState(otherPos);
				
				return otherState.isAir();
			}
			return false;
		}
	}
}
