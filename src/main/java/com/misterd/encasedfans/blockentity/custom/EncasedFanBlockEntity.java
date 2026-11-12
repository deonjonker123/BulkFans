package com.misterd.encasedfans.blockentity.custom;

import com.misterd.encasedfans.block.custom.EncasedFanBlock;
import com.misterd.encasedfans.blockentity.EFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EncasedFanBlockEntity extends BlockEntity {
    private static final float SPEED_EASING = 0.1F;

    private float angle;
    private float prevAngle;
    private float speed;

    public EncasedFanBlockEntity(BlockPos pos, BlockState state) {
        super(EFBlockEntities.ENCASED_FAN.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, EncasedFanBlockEntity fan) {
        float target = targetSpeed(state.getValue(EncasedFanBlock.POWER));
        fan.speed += (target - fan.speed) * SPEED_EASING;
        if (target == 0 && fan.speed < 0.05F) {
            fan.speed = 0;
        }
        fan.prevAngle = fan.angle;
        fan.angle += fan.speed;
        if (fan.angle >= 360) {
            fan.angle -= 360;
            fan.prevAngle -= 360;
        }
    }

    private static float targetSpeed(int power) {
        return power <= 0 ? 0 : 4 + power * 2;
    }

    public float getAngle(float partialTick) {
        return Mth.lerp(partialTick, prevAngle, angle);
    }
}
