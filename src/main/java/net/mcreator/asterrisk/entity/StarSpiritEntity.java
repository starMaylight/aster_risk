package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.registry.ModEntities;
import net.mcreator.asterrisk.init.AsterRiskModItems;
import net.mcreator.asterrisk.init.AsterRiskModEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 星の精霊 - Star Spirit
 * 友好的な浮遊するMob。近くのプレイヤーにバフを与える。
 * 星の欠片で信頼度が上がり、強力なバフを与える。
 */
public class StarSpiritEntity extends PathfinderMob {
    
    private int buffTimer = 0;
    private int particleTimer = 0;
    private static final int BUFF_INTERVAL = 200; // 10秒ごと
    private static final double BUFF_RANGE = 8.0D;

    public StarSpiritEntity(EntityType<? extends StarSpiritEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true); // 浮遊
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.25D)
            .add(Attributes.FOLLOW_RANGE, 20.0D)
            .add(Attributes.FLYING_SPEED, 0.4D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        
        // 浮遊動作
        if (!this.onGround() && this.getDeltaMovement().y < 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
        }
        
        // パーティクル（常時）
        if (this.level().isClientSide()) {
            particleTimer++;
            if (particleTimer >= 5) {
                particleTimer = 0;
                this.level().addParticle(
                    ParticleTypes.FIREWORK,
                    this.getRandomX(0.6D),
                    this.getRandomY(),
                    this.getRandomZ(0.6D),
                    0, 0, 0
                );
            }
        }
        
        // バフを定期的に付与
        if (!this.level().isClientSide()) {
            buffTimer++;
            if (buffTimer >= BUFF_INTERVAL) {
                buffTimer = 0;
                applyBuffToNearbyPlayers();
            }
        }
    }

    private void applyBuffToNearbyPlayers() {
        AABB searchBox = this.getBoundingBox().inflate(BUFF_RANGE);
        List<Player> players = this.level().getEntitiesOfClass(Player.class, searchBox);
        
        for (Player player : players) {
            // 星屑の加護を30秒付与
            player.addEffect(new MobEffectInstance(
                AsterRiskModEffects.STARDUST_PROTECTION.get(),
                20 * 30, // 30秒
                0
            ));
            
            // パーティクル
            for (int i = 0; i < 10; i++) {
                this.level().addParticle(
                    ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 1.0D, player.getZ(),
                    (this.random.nextDouble() - 0.5D) * 0.3D,
                    this.random.nextDouble() * 0.3D,
                    (this.random.nextDouble() - 0.5D) * 0.3D
                );
            }
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false; // 落下ダメージなし
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // 星の欠片を与えると即座にバフ
        if (stack.is(AsterRiskModItems.STARFLAGMENT.get())) {
            if (!this.level().isClientSide()) {
                // 強化されたバフを付与
                player.addEffect(new MobEffectInstance(
                    AsterRiskModEffects.STARDUST_PROTECTION.get(),
                    20 * 60 * 3, // 3分
                    1 // レベル2
                ));
                
                // マナも回復
                player.getCapability(net.mcreator.asterrisk.mana.LunarManaCapability.LUNAR_MANA).ifPresent(mana -> {
                    mana.addMana(20.0f);
                });
                
                // 派手なパーティクル
                for (int i = 0; i < 20; i++) {
                    this.level().addParticle(
                        ParticleTypes.FIREWORK,
                        this.getX(), this.getY() + 0.5D, this.getZ(),
                        (this.random.nextDouble() - 0.5D) * 0.8D,
                        this.random.nextDouble() * 0.5D,
                        (this.random.nextDouble() - 0.5D) * 0.8D
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

    @Override
    public boolean isPushable() {
        return false;
    }
}
