package com.lion.villagersplus.client;

import com.lion.villagersplus.blockentities.HorticulturistTableBlockEntity;
import com.lion.villagersplus.blocks.HorticulturistTableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/** Draws the plants held inside a flower tub. */
public class HorticulturistTableRenderer implements BlockEntityRenderer<HorticulturistTableBlockEntity> {

    /** Corner positions for up to four small plants, scaled to fit the tub. */
    private static final float[] X_OFFSET = {0.28F, 0.72F, 0.28F, 0.72F};
    private static final float[] Z_OFFSET = {0.28F, 0.28F, 0.72F, 0.72F};
    private static final float SMALL_SCALE = 0.45F;
    /** Height of the tub rim, where plants sit. */
    private static final float TOP = 0.95F;

    private final BlockRenderDispatcher dispatcher;

    public HorticulturistTableRenderer(BlockEntityRendererProvider.Context context) {
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(HorticulturistTableBlockEntity tub, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        Level level = tub.getLevel();
        BlockState state = tub.getBlockState();
        if (level == null || !(state.getBlock() instanceof HorticulturistTableBlock)) {
            return;
        }

        BlockPos pos = tub.getBlockPos();

        if (state.getValue(HorticulturistTableBlock.IS_TALL_FLOWER)) {
            renderTall(tub.getItem(0), level, pos, poseStack, buffers, packedOverlay);
            return;
        }

        int flowers = state.getValue(HorticulturistTableBlock.FLOWERS);
        for (int slot = 0; slot < Math.min(flowers, 4); slot++) {
            Block plant = plantFor(tub.getItem(slot));
            if (plant == null) {
                continue;
            }
            poseStack.pushPose();
            // Single plants sit centred; multiples are spread across the tub.
            float x = flowers == 1 ? 0.5F : X_OFFSET[slot];
            float z = flowers == 1 ? 0.5F : Z_OFFSET[slot];
            float scale = flowers == 1 ? 0.6F : SMALL_SCALE;
            poseStack.translate(x, TOP, z);
            poseStack.scale(scale, scale, scale);
            poseStack.translate(-0.5D, 0.0D, -0.5D);
            renderBlock(plant.defaultBlockState(), level, pos, poseStack, buffers, packedOverlay);
            poseStack.popPose();
        }
    }

    /** A tall plant fills the tub, drawn as its lower and upper halves. */
    private void renderTall(ItemStack stack, Level level, BlockPos pos, PoseStack poseStack,
                            MultiBufferSource buffers, int packedOverlay) {
        Block plant = plantFor(stack);
        if (plant == null) {
            return;
        }
        BlockState base = plant.defaultBlockState();
        poseStack.pushPose();
        poseStack.translate(0.0D, TOP, 0.0D);
        if (base.hasProperty(DoublePlantBlock.HALF)) {
            renderBlock(base.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER),
                    level, pos, poseStack, buffers, packedOverlay);
            poseStack.translate(0.0D, 1.0D, 0.0D);
            renderBlock(base.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER),
                    level, pos, poseStack, buffers, packedOverlay);
        } else {
            renderBlock(base, level, pos, poseStack, buffers, packedOverlay);
        }
        poseStack.popPose();
    }

    private void renderBlock(BlockState state, Level level, BlockPos pos, PoseStack poseStack,
                             MultiBufferSource buffers, int packedOverlay) {
        this.dispatcher.getModelRenderer().tesselateBlock(
                level,
                this.dispatcher.getBlockModel(state),
                state,
                pos,
                poseStack,
                buffers.getBuffer(RenderType.cutoutMipped()),
                false,
                RandomSource.create(),
                state.getSeed(pos),
                packedOverlay);
    }

    private static Block plantFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Block block = Block.byItem(stack.getItem());
        return block == Blocks.AIR ? null : block;
    }
}
