package com.lion.villagersplus.client;

import com.lion.villagersplus.blockentities.OceanographerTableBlockEntity;
import com.lion.villagersplus.blocks.OceanographerTableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

/** Draws the corals and the bucketed fish held inside an aquarium. */
public class OceanographerTableRenderer implements BlockEntityRenderer<OceanographerTableBlockEntity> {

    /** Corner positions for the four coral slots. */
    private static final float[] X_OFFSET = {0.28F, 0.72F, 0.28F, 0.72F};
    private static final float[] Z_OFFSET = {0.28F, 0.28F, 0.72F, 0.72F};
    private static final float CORAL_SCALE = 0.4F;
    /** Corals sit on the floor of the tank, the fish swims above them. */
    private static final float FLOOR = 0.15F;
    private static final float FISH_HEIGHT = 0.5F;

    /** Bucket item -> what is inside it. MobBucketItem keeps its type private. */
    private static final Map<Item, EntityType<?>> BUCKET_CONTENTS = Map.of(
            Items.COD_BUCKET, EntityType.COD,
            Items.SALMON_BUCKET, EntityType.SALMON,
            Items.TROPICAL_FISH_BUCKET, EntityType.TROPICAL_FISH,
            Items.PUFFERFISH_BUCKET, EntityType.PUFFERFISH,
            Items.AXOLOTL_BUCKET, EntityType.AXOLOTL,
            Items.TADPOLE_BUCKET, EntityType.TADPOLE);

    /** Display entities are reused rather than rebuilt every frame. */
    private final Map<EntityType<?>, Entity> displayEntities = new HashMap<>();

    private final BlockRenderDispatcher blocks;
    private final EntityRenderDispatcher entities;

    public OceanographerTableRenderer(BlockEntityRendererProvider.Context context) {
        this.blocks = context.getBlockRenderDispatcher();
        this.entities = context.getEntityRenderer();
    }

    @Override
    public void render(OceanographerTableBlockEntity tank, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {
        Level level = tank.getLevel();
        BlockState state = tank.getBlockState();
        if (level == null || !(state.getBlock() instanceof OceanographerTableBlock)) {
            return;
        }

        BlockPos pos = tank.getBlockPos();

        for (int slot = 0; slot < 4; slot++) {
            Block coral = coralFor(tank.getItem(slot));
            if (coral == null) {
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(X_OFFSET[slot], FLOOR, Z_OFFSET[slot]);
            poseStack.scale(CORAL_SCALE, CORAL_SCALE, CORAL_SCALE);
            poseStack.translate(-0.5D, 0.0D, -0.5D);
            renderBlock(coral.defaultBlockState(), level, pos, poseStack, buffers, packedOverlay);
            poseStack.popPose();
        }

        renderFish(tank.getItem(OceanographerTableBlockEntity.FISH_SLOT), level, partialTick,
                poseStack, buffers, packedLight);
    }

    private void renderFish(ItemStack bucket, Level level, float partialTick, PoseStack poseStack,
                            MultiBufferSource buffers, int packedLight) {
        if (bucket.isEmpty()) {
            return;
        }
        EntityType<?> type = BUCKET_CONTENTS.get(bucket.getItem());
        if (type == null) {
            return;
        }

        Entity fish = this.displayEntities.computeIfAbsent(type, t -> t.create(level));
        if (fish == null) {
            return;
        }

        // Slow drift so the tank does not look frozen.
        float time = (level.getGameTime() + partialTick) * 2.0F;
        float yaw = time % 360.0F;
        float bob = Mth.sin(time / 20.0F) * 0.05F;

        poseStack.pushPose();
        poseStack.translate(0.5D, FISH_HEIGHT + bob, 0.5D);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        try {
            this.entities.render(fish, 0.0D, 0.0D, 0.0D, yaw, partialTick, poseStack, buffers, packedLight);
        } catch (Exception ignored) {
            // A modded or unusual entity failing to draw must not break the whole tank.
        }
        poseStack.popPose();
    }

    private void renderBlock(BlockState state, Level level, BlockPos pos, PoseStack poseStack,
                             MultiBufferSource buffers, int packedOverlay) {
        this.blocks.getModelRenderer().tesselateBlock(
                level,
                this.blocks.getBlockModel(state),
                state,
                pos,
                poseStack,
                buffers.getBuffer(RenderType.cutoutMipped()),
                false,
                RandomSource.create(),
                state.getSeed(pos),
                packedOverlay);
    }

    private static Block coralFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        Block block = Block.byItem(stack.getItem());
        return block == Blocks.AIR ? null : block;
    }
}
