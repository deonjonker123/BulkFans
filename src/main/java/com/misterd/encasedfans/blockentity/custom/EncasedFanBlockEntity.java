package com.misterd.encasedfans.blockentity.custom;

import com.misterd.encasedfans.block.custom.EncasedFanBlock;
import com.misterd.encasedfans.blockentity.EFBlockEntities;
import com.misterd.encasedfans.processing.FanProcessingType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EncasedFanBlockEntity extends BlockEntity {
    private static final float SPEED_EASING = 0.1F;
    private static final int AIRFLOW_REFRESH_INTERVAL = 10;
    private static final double MAX_ACCELERATION = 0.15;
    private static final double MAX_FLOW_SPEED = 0.5;
    private static final int IGNITE_TICKS = 120;
    private static final int DARKNESS_TICKS = 60;
    private static final int DARKNESS_REFRESH_BELOW = 10;

    private float angle;
    private float prevAngle;
    private float speed;

    private FanProcessingType[] airflow = new FanProcessingType[0];
    private int airflowRange;
    private int airflowRefreshTimer;
    private BlockState airflowState;

    public EncasedFanBlockEntity(BlockPos pos, BlockState state) {
        super(EFBlockEntities.ENCASED_FAN.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, EncasedFanBlockEntity fan) {
        fan.tickSpin(state);
        fan.tickAirflow(level, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EncasedFanBlockEntity fan) {
        fan.tickAirflow(level, pos, state);
    }

    private void tickSpin(BlockState state) {
        float target = targetSpeed(state.getValue(EncasedFanBlock.POWER));
        if (state.getValue(EncasedFanBlock.PULL)) {
            target = -target;
        }
        speed += (target - speed) * SPEED_EASING;
        if (target == 0 && Math.abs(speed) < 0.05F) {
            speed = 0;
        }
        prevAngle = angle;
        angle += speed;
        if (angle >= 360) {
            angle -= 360;
            prevAngle -= 360;
        } else if (angle < 0) {
            angle += 360;
            prevAngle += 360;
        }
    }

    private static float targetSpeed(int power) {
        return power <= 0 ? 0 : 4 + power * 2;
    }

    public float getAngle(float partialTick) {
        return Mth.lerp(partialTick, prevAngle, angle);
    }

    private void tickAirflow(Level level, BlockPos pos, BlockState state) {
        int power = state.getValue(EncasedFanBlock.POWER);
        if (power == 0) {
            airflow = new FanProcessingType[0];
            airflowState = null;
            return;
        }
        if (state != airflowState || --airflowRefreshTimer <= 0) {
            refreshAirflow(level, pos, state);
        }
        if (airflow.length == 0) {
            return;
        }

        Direction facing = state.getValue(EncasedFanBlock.FACING);
        boolean pull = state.getValue(EncasedFanBlock.PULL);
        AABB area = AABB.encapsulatingFullBlocks(pos.relative(facing), pos.relative(facing, airflow.length));

        for (Entity entity : level.getEntities((Entity) null, area, entity -> !entity.isSpectator())) {
            double distance = distanceFromFan(pos, facing, entity);
            if (entity.isLocalInstanceAuthoritative()) {
                pushEntity(entity, facing, pull, distance);
            }
            if (!level.isClientSide()) {
                applyEffects(entity, typeAt(distance));
            }
        }
    }

    private void refreshAirflow(Level level, BlockPos pos, BlockState state) {
        airflowState = state;
        airflowRefreshTimer = AIRFLOW_REFRESH_INTERVAL;
        airflowRange = EncasedFanBlock.rangeFor(state.getValue(EncasedFanBlock.POWER));
        Direction facing = state.getValue(EncasedFanBlock.FACING);

        FanProcessingType[] cells = new FanProcessingType[airflowRange];
        FanProcessingType current = FanProcessingType.NONE;
        BlockPos.MutableBlockPos cursor = pos.mutable();
        int length = 0;
        while (length < airflowRange) {
            cursor.move(facing);
            BlockState cellState = level.getBlockState(cursor);
            FanProcessingType catalyst = FanProcessingType.fromCatalyst(cellState);
            if (catalyst != null) {
                current = catalyst;
            } else if (blocksAirflow(level, cursor, cellState, facing)) {
                break;
            }
            cells[length++] = current;
        }

        airflow = new FanProcessingType[length];
        System.arraycopy(cells, 0, airflow, 0, length);
    }

    private static boolean blocksAirflow(Level level, BlockPos pos, BlockState state, Direction facing) {
        if (state.isAir()) {
            return false;
        }
        if (state.getBlock() instanceof TrapDoorBlock) {
            Direction.Axis plateNormal = state.getValue(TrapDoorBlock.OPEN) ? state.getValue(TrapDoorBlock.FACING).getAxis() : Direction.Axis.Y;
            return plateNormal == facing.getAxis();
        }
        if (state.getBlock() instanceof FenceGateBlock) {
            return !state.getValue(FenceGateBlock.OPEN) && state.getValue(FenceGateBlock.FACING).getAxis() == facing.getAxis();
        }
        return Block.isFaceFull(state.getCollisionShape(level, pos), facing.getOpposite());
    }

    private static double distanceFromFan(BlockPos pos, Direction facing, Entity entity) {
        Vec3 offset = entity.getBoundingBox().getCenter().subtract(pos.getCenter());
        return Math.max(0, offset.dot(facing.getUnitVec3()) - 0.5);
    }

    private FanProcessingType typeAt(double distance) {
        int cell = Mth.clamp((int) distance, 0, airflow.length - 1);
        return airflow[cell];
    }

    private void pushEntity(Entity entity, Direction facing, boolean pull, double distance) {
        double falloff = 1 - 0.5 * Math.min(distance / airflowRange, 1);
        Vec3 direction = pull ? facing.getUnitVec3().reverse() : facing.getUnitVec3();
        double cap = MAX_FLOW_SPEED * falloff;
        double along = entity.getDeltaMovement().dot(direction);
        if (along < cap) {
            entity.push(direction.scale(Math.min(MAX_ACCELERATION * falloff, cap - along)));
        }
        if (facing == Direction.UP && !pull) {
            entity.resetFallDistance();
        }
    }

    private static void applyEffects(Entity entity, FanProcessingType type) {
        if (entity instanceof ItemEntity) {
            return;
        }
        if (type.ignitesEntities() && !entity.fireImmune()) {
            entity.igniteForTicks(IGNITE_TICKS);
        }
        if (type.extinguishesEntities() && entity.isOnFire()) {
            entity.clearFire();
            float pitch = 1.6F + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.4F;
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, entity.getSoundSource(), 0.7F, pitch);
        }
        if (type.darkensEntities() && entity instanceof LivingEntity living) {
            MobEffectInstance darkness = living.getEffect(MobEffects.DARKNESS);
            if (darkness == null || darkness.getDuration() < DARKNESS_REFRESH_BELOW) {
                living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, DARKNESS_TICKS, 0, false, false));
            }
        }
    }
}