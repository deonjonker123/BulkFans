package com.misterd.encasedfans.blockentity.custom;

import com.misterd.encasedfans.block.custom.EncasedFanBlock;
import com.misterd.encasedfans.block.custom.TrayBlock;
import com.misterd.encasedfans.blockentity.EFBlockEntities;
import com.misterd.encasedfans.config.EFConfig;
import com.misterd.encasedfans.processing.EntityEffect;
import com.misterd.encasedfans.processing.FanCatalysts;
import com.misterd.encasedfans.processing.FanProcessing;
import com.misterd.encasedfans.processing.ProcessingType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
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
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EncasedFanBlockEntity extends BlockEntity {
    private static final int EFFECT_REFRESH_BELOW = 10;
    private static final double PARTICLE_SPREAD = 0.8;
    private static final ParticleOptions AIR_PARTICLE = ParticleTypes.CLOUD;

    private float angle;
    private float prevAngle;
    private float speed;

    private final List<@Nullable Holder<ProcessingType>> airflow = new ArrayList<>();
    private int airflowRange;
    private int airflowRefreshTimer;
    private BlockState airflowState;
    private int processTimer;

    public EncasedFanBlockEntity(BlockPos pos, BlockState state) {
        super(EFBlockEntities.ENCASED_FAN.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, EncasedFanBlockEntity fan) {
        fan.tickSpin(state);
        fan.tickAirflow(level, pos, state);
        fan.spawnParticles(level, pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EncasedFanBlockEntity fan) {
        fan.tickAirflow(level, pos, state);
        if (--fan.processTimer <= 0 && level instanceof ServerLevel serverLevel) {
            int interval = EFConfig.SERVER.processInterval.get();
            fan.processTimer = interval;
            fan.processItems(serverLevel, pos, state, interval);
        }
    }

    private void tickSpin(BlockState state) {
        float target = targetSpeed(state.getValue(EncasedFanBlock.POWER));
        if (state.getValue(EncasedFanBlock.PULL)) {
            target = -target;
        }
        speed += (target - speed) * EFConfig.CLIENT.spinEasing.get().floatValue();
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
        if (power <= 0) {
            return 0;
        }
        return (float) (EFConfig.CLIENT.spinBaseSpeed.get() + EFConfig.CLIENT.spinSpeedPerPower.get() * power);
    }

    public float getAngle(float partialTick) {
        return Mth.lerp(partialTick, prevAngle, angle);
    }

    private void tickAirflow(Level level, BlockPos pos, BlockState state) {
        int power = state.getValue(EncasedFanBlock.POWER);
        if (power == 0) {
            airflow.clear();
            airflowState = null;
            return;
        }
        if (state != airflowState || --airflowRefreshTimer <= 0) {
            refreshAirflow(level, pos, state);
        }
        if (airflow.isEmpty()) {
            return;
        }

        Direction facing = state.getValue(EncasedFanBlock.FACING);
        boolean pull = state.getValue(EncasedFanBlock.PULL);
        AABB area = airflowArea(pos, facing);

        for (Entity entity : level.getEntities((Entity) null, area, entity -> !entity.isSpectator())) {
            double distance = distanceFromFan(pos, facing, entity);
            if (entity.isLocalInstanceAuthoritative()) {
                pushEntity(entity, facing, pull, distance);
            }
            Holder<ProcessingType> type = typeAt(distance);
            if (!level.isClientSide() && type != null) {
                applyEffects(entity, type.value());
            }
        }
    }

    private void processItems(ServerLevel level, BlockPos pos, BlockState state, int interval) {
        if (airflow.isEmpty()) {
            return;
        }
        Direction facing = state.getValue(EncasedFanBlock.FACING);
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, airflowArea(pos, facing), ItemEntity::isAlive)) {
            Holder<ProcessingType> type = typeAt(distanceFromFan(pos, facing, item));
            if (type != null) {
                FanProcessing.tick(level, item, type, interval);
            }
        }
        BlockPos.MutableBlockPos cursor = pos.mutable();
        for (Holder<ProcessingType> type : airflow) {
            cursor.move(facing);
            if (type != null && level.getBlockEntity(cursor) instanceof TrayBlockEntity tray) {
                tray.process(level, type, interval);
            }
        }
    }

    private AABB airflowArea(BlockPos pos, Direction facing) {
        return AABB.encapsulatingFullBlocks(pos.relative(facing), pos.relative(facing, airflow.size()));
    }

    private void spawnParticles(Level level, BlockPos pos, BlockState state) {
        if (airflow.isEmpty()) {
            return;
        }
        RandomSource random = level.getRandom();
        Direction facing = state.getValue(EncasedFanBlock.FACING);
        Vec3 axis = facing.getUnitVec3();
        Vec3 flow = state.getValue(EncasedFanBlock.PULL) ? axis.reverse() : axis;
        float chance = EFConfig.CLIENT.particleChance.get().floatValue();
        int attempts = Math.max(1, airflow.size() / EFConfig.CLIENT.blocksPerParticle.get());

        for (int i = 0; i < attempts; i++) {
            if (random.nextFloat() >= chance) {
                continue;
            }
            double distance = random.nextDouble() * airflow.size();
            Vec3 spread = new Vec3(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5).scale(PARTICLE_SPREAD);
            spread = spread.subtract(axis.scale(spread.dot(axis)));
            Vec3 position = pos.getCenter().add(axis.scale(0.5 + distance)).add(spread);
            Vec3 velocity = flow.scale(0.05 + random.nextDouble() * 0.1);
            Holder<ProcessingType> type = typeAt(distance);
            ParticleOptions particle = type == null ? AIR_PARTICLE : type.value().particle();
            level.addParticle(particle, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        }
    }

    private void refreshAirflow(Level level, BlockPos pos, BlockState state) {
        airflowState = state;
        airflowRefreshTimer = EFConfig.SERVER.airflowRefreshInterval.get();
        airflowRange = EncasedFanBlock.rangeFor(state.getValue(EncasedFanBlock.POWER));
        Direction facing = state.getValue(EncasedFanBlock.FACING);

        airflow.clear();
        Holder<ProcessingType> current = null;
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (airflow.size() < airflowRange) {
            cursor.move(facing);
            BlockState cellState = level.getBlockState(cursor);
            Optional<Holder<ProcessingType>> catalyst = FanCatalysts.find(level, cellState);
            if (catalyst.isPresent()) {
                current = catalyst.get();
            } else if (blocksAirflow(level, cursor, cellState, facing)) {
                break;
            }
            airflow.add(current);
        }
    }

    private static boolean blocksAirflow(Level level, BlockPos pos, BlockState state, Direction facing) {
        if (state.isAir() || state.getBlock() instanceof TrayBlock) {
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

    private @Nullable Holder<ProcessingType> typeAt(double distance) {
        int cell = Mth.clamp((int) distance, 0, airflow.size() - 1);
        return airflow.get(cell);
    }

    private void pushEntity(Entity entity, Direction facing, boolean pull, double distance) {
        double falloff = 1 - EFConfig.SERVER.pushFalloff.get() * Math.min(distance / airflowRange, 1);
        Vec3 direction = pull ? facing.getUnitVec3().reverse() : facing.getUnitVec3();
        double cap = EFConfig.SERVER.maxFlowSpeed.get() * falloff;
        double along = entity.getDeltaMovement().dot(direction);
        if (along < cap) {
            entity.push(direction.scale(Math.min(EFConfig.SERVER.pushAcceleration.get() * falloff, cap - along)));
        }
        if (facing == Direction.UP && !pull) {
            entity.resetFallDistance();
        }
    }

    private static void applyEffects(Entity entity, ProcessingType type) {
        if (entity instanceof ItemEntity) {
            return;
        }
        if (type.ignitesEntities() && !entity.fireImmune()) {
            entity.igniteForTicks(EFConfig.SERVER.igniteTicks.get());
        }
        if (type.extinguishesEntities() && entity.isOnFire()) {
            entity.clearFire();
            float pitch = 1.6F + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.4F;
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXTINGUISH_FIRE, entity.getSoundSource(), 0.7F, pitch);
        }
        if (!type.effects().isEmpty() && entity instanceof LivingEntity living) {
            for (EntityEffect effect : type.effects()) {
                MobEffectInstance current = living.getEffect(effect.effect());
                if (current == null || current.getDuration() < EFFECT_REFRESH_BELOW) {
                    int duration = effect.duration().orElseGet(EFConfig.SERVER.effectTicks::get);
                    living.addEffect(new MobEffectInstance(effect.effect(), duration, effect.amplifier(), false, false));
                }
            }
        }
    }
}