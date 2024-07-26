package com.bluefire.rafts.mixins;

import com.bluefire.rafts.Rafts;
import com.bluefire.rafts.RaftsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(EntityBoat.class)
public abstract class BoatMixin extends Entity {

    public BoatMixin(World worldIn)
    {
        super(worldIn);
    }

    /*
    @Shadow
    protected float jumpPower;

    @Shadow
    protected abstract void makeHorseRear();


    public float wrapAngle(float angle) {
        return ((angle+180f)%360f) - 180f;
    }

    public float angleToXZ(float x, float z) {
        return ((float) MathHelper.atan2(x, z) * 57.2957812f) - 90f;
    }

    public float angleDifference(float angle1, float angle2) {
        return this.wrapAngle((angle2-angle1+360)%360f);
    }

    public float rotateToward(float current, float target, float percent, float minRate, float maxRate) {
        float angleDiff = this.angleDifference(current, target);
        float neg = angleDiff > 0f ? 1f : -1f;
        angleDiff = Math.abs(angleDiff);
        if (angleDiff <= minRate)
            return target;
        angleDiff = Math.min(Math.max(angleDiff*percent, minRate), maxRate);
        return current + angleDiff*neg;
    }

     */

/*
    @Inject(method = "updatePassenger", cancellable = true, at = @At("HEAD"))
    public void updatePassengerMixin(Entity passenger, CallbackInfo ci)
    {
        super.updatePassenger(passenger);
        if (this.isPassenger(passenger))
        {
            double px = this.posX;
            double py = this.posY;
            double pz = this.posZ;

            if (passenger instanceof EntityLiving)
            {
                EntityLiving entityliving = (EntityLiving)passenger;
                this.renderYawOffset = entityliving.renderYawOffset;
            }
            if (passenger instanceof EntityPlayer)
            {
                double leanMult = (double)(Math.max(passenger.rotationPitch+RaftsConfig.LEAN_ANGLE_START, 0f))/(90d+RaftsConfig.LEAN_ANGLE_START);
                double leanOut = (double)((Math.abs((Math.min(angleDifference(this.rotationYaw, passenger.rotationYaw), 60f)/60f)*0.5f))+0.25d)*leanMult;
                double leanVert = leanMult*0.4d;


                EntityPlayer player = (EntityPlayer)passenger;
                if (!player.getActiveItemStack().isEmpty() && player.getActiveItemStack().getItemUseAction() == EnumAction.BOW) {
                    leanOut *= RaftsConfig.LEAN_MULT_BOW;;
                    leanVert *= RaftsConfig.LEAN_VERT_MULT_BOW;
                } else {
                    leanOut *= RaftsConfig.LEAN_MULT;
                    leanVert *= RaftsConfig.LEAN_VERT_MULT;
                }
                leanVert = Math.min(leanVert, 0.3875d);
                leanOut = Math.min(leanOut, 1.0d);

                float lx = MathHelper.sin(passenger.rotationYaw * 0.017453292F)*-1f;
                float lz = MathHelper.cos(passenger.rotationYaw * 0.017453292F);

                px += lx*leanOut;
                py += leanVert + 0.02d;
                pz += lz*leanOut;

                if (this.world.collidesWithAnyBlock(new AxisAlignedBB(px-0.35d, py+1.0d, pz-0.35d, px+0.35d, py+3.1d, pz+0.35d))) {
                    px = this.posX;
                    pz = this.posZ;
                }
            }

            if (this.prevRearingAmount > 0.0F)
            {
                float f3 = MathHelper.sin(this.renderYawOffset * 0.017453292F);
                float f = MathHelper.cos(this.renderYawOffset * 0.017453292F);
                float f1 = 0.7F * this.prevRearingAmount;
                float f2 = 0.15F * this.prevRearingAmount;
                passenger.setPosition(px + (double)(f1 * f3), py + this.getMountedYOffset() + passenger.getYOffset() + (double)f2, pz - (double)(f1 * f));

                if (passenger instanceof EntityLivingBase)
                {
                    ((EntityLivingBase)passenger).renderYawOffset = this.renderYawOffset;
                }
            } else {
                passenger.setPosition(px, py + this.getMountedYOffset() + passenger.getYOffset(), pz);
            }
        }
        ci.cancel();
    }

    @Inject(method = "travel", cancellable = true, at = @At("HEAD"))
    public void travel(float strafe, float vertical, float forward, CallbackInfo ci)
    {
        if (this.isBeingRidden() && this.canBeSteered() && this.isHorseSaddled())
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)this.getControllingPassenger();
            // sprinting
            if (RaftsConfig.ENABLE_SPRINTING && !this.isServerWorld() && entitylivingbase instanceof EntityPlayerSP) {
                EntityPlayerSP psp = (EntityPlayerSP) entitylivingbase;
                if (!this.isSprinting() && entitylivingbase.moveForward > 0f && !psp.isPotionActive(MobEffects.BLINDNESS) && Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown())
                {
                    this.setSprinting(true);
                    Rafts.instance.sendHorseSprintingPacket();
                }
            }
            if (this.isSprinting()) {
                if(entitylivingbase.moveForward <= 0f) {
                    this.setSprinting(false);
                }
            }


            // get target angle and speed
            float forwardA = entitylivingbase.moveForward;
            float strafeA = entitylivingbase.moveStrafing;
            float targetAngle = 0.0f;
            float targetSpeed = Math.max(Math.abs(forwardA), Math.abs(strafeA));

            this.rotationYaw = wrapAngle(this.rotationYaw);

            if (targetSpeed > 0f) {
                if (forwardA >= 0f) {
                    forwardA = Math.max(forwardA, 0.01f);
                    targetAngle = this.wrapAngle(this.angleToXZ(forwardA, strafeA) + entitylivingbase.rotationYaw);
                } else {
                    targetAngle = this.wrapAngle(this.angleToXZ(forwardA, strafeA) + entitylivingbase.rotationYaw - 180f);
                    targetSpeed *= RaftsConfig.BACKPEDAL_SPEED_MULT * -1f;
                }
            } else {
                targetSpeed = 0f;
                float angleDiff = this.angleDifference(this.rotationYaw, entitylivingbase.rotationYaw);
                if(Math.abs(angleDiff) > 70f) {
                    targetAngle = entitylivingbase.rotationYaw;
                } else {
                    targetAngle = this.rotationYaw;
                }
            }

            float angleSlow = Math.max(1f-(Math.abs(this.angleDifference(this.rotationYaw, targetAngle))/180f), 0f);
            if(this.currentSpeed < targetSpeed) {
                this.currentSpeed += RaftsConfig.ACCELERATION;
                if(this.currentSpeed > targetSpeed) {
                    this.currentSpeed = targetSpeed;
                }
            } else if(this.currentSpeed > targetSpeed) {
                this.currentSpeed -= RaftsConfig.DECELERATION;
                if(this.currentSpeed < targetSpeed) {
                    this.currentSpeed = targetSpeed;
                }
            }

            //forward = targetSpeed*angleSlow;
            forward = angleSlow*this.currentSpeed;
            if(!this.isSprinting()) {
                this.rotationYaw = this.wrapAngle(this.rotateToward(this.rotationYaw, targetAngle, (float)RaftsConfig.ANGLE_CHANGE_PERCENT, (float)RaftsConfig.ANGLE_CHANGE_MIN, (float)RaftsConfig.ANGLE_CHANGE_MAX));
            } else {
                this.rotationYaw = this.wrapAngle(this.rotateToward(this.rotationYaw, targetAngle, (float)RaftsConfig.ANGLE_CHANGE_PERCENT_SPRINTING, (float)RaftsConfig.ANGLE_CHANGE_MIN_SPRINTING, (float)RaftsConfig.ANGLE_CHANGE_MAX_SPRINTING));
            }

            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = entitylivingbase.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);

            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.renderYawOffset;
            //strafe = entitylivingbase.moveStrafing;
            strafe = 0f;
            //forward = Math.max(Math.abs(forwardA), Math.abs(strafeA));

            if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround)
            {
                this.motionY = this.getHorseJumpStrength() * (double)this.jumpPower;

                if (this.isPotionActive(MobEffects.JUMP_BOOST))
                {
                    this.motionY += (double)((float)(this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
                }

                this.setHorseJumping(true);
                this.isAirBorne = true;

                if (forward > 0.0F)
                {
                    float f = MathHelper.sin(this.rotationYaw * 0.017453292F);
                    float f1 = MathHelper.cos(this.rotationYaw * 0.017453292F);
                    this.motionX += (double)(-0.4F * f * this.jumpPower);
                    this.motionZ += (double)(0.4F * f1 * this.jumpPower);
                    this.playSound(SoundEvents.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
                }

                this.jumpPower = 0.0F;
            }

            this.jumpMovementFactor = this.getAIMoveSpeed() * (float)RaftsConfig.JUMP_MOVEMENT_FACTOR;

            if (this.canPassengerSteer())
            {
                this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
                super.travel(strafe, vertical, forward);
            }
            else if (entitylivingbase instanceof EntityPlayer)
            {
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            if (this.onGround)
            {
                this.jumpPower = 0.0F;
                this.setHorseJumping(false);
            }

            this.prevLimbSwingAmount = this.limbSwingAmount;
            double d1 = this.posX - this.prevPosX;
            double d0 = this.posZ - this.prevPosZ;
            float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

            if (f2 > 1.0F)
            {
                f2 = 1.0F;
            }

            this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
            this.limbSwing += this.limbSwingAmount;
        }
        else
        {
            if (this.isSprinting()) {
                this.setSprinting(false);
            }
            this.jumpMovementFactor = 0.02F;
            super.travel(strafe, vertical, forward);
        }
        ci.cancel();
    }

 */

}
