package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.registry.ModEntities;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import javax.annotation.Nullable;

/**
 * 月うさぎ - Moon Rabbit
 * 友好的なMob。夜間に活発になり、月光石やルナーダストをドロップする。
 * 餌：ニンジン、月光石
 */
public class MoonRabbitEntity extends Animal {
    
    private int particleTimer = 0;

    public MoonRabbitEntity(EntityType<? extends MoonRabbitEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(
            net.minecraft.world.item.Items.CARROT,
            net.minecraft.world.item.Items.GOLDEN_CARROT,
            AsterRiskModItems.MOONSTONE.get()
        ), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 8.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.3D)
            .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        
        // 夜間にパーティクルを発生
        if (this.level().isClientSide()) {
            long dayTime = this.level().getDayTime() % 24000;
            boolean isNight = dayTime >= 13000 && dayTime < 23000;
            
            if (isNight) {
                particleTimer++;
                if (particleTimer >= 10) {
                    particleTimer = 0;
                    this.level().addParticle(
                        ParticleTypes.END_ROD,
                        this.getRandomX(0.5D),
                        this.getRandomY() + 0.5D,
                        this.getRandomZ(0.5D),
                        0, 0.02D, 0
                    );
                }
            }
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.CARROT) || 
               stack.is(net.minecraft.world.item.Items.GOLDEN_CARROT) ||
               stack.is(AsterRiskModItems.MOONSTONE.get());
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.MOON_RABBIT.get().create(level);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.RABBIT_DEATH;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // 月光石を与えると少量のマナを回復
        if (stack.is(AsterRiskModItems.MOONSTONE.get())) {
            if (!this.level().isClientSide()) {
                player.getCapability(net.mcreator.asterrisk.mana.LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                    mana.addMana(5.0f);
                });
                
                // パーティクル
                for (int i = 0; i < 5; i++) {
                    this.level().addParticle(
                        ParticleTypes.END_ROD,
                        this.getX(), this.getY() + 0.5D, this.getZ(),
                        (this.random.nextDouble() - 0.5D) * 0.5D,
                        this.random.nextDouble() * 0.5D,
                        (this.random.nextDouble() - 0.5D) * 0.5D
                    );
                }
                
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        
        return super.mobInteract(player, hand);
    }
}
