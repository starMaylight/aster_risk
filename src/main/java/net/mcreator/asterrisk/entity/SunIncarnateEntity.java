package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.AsterRiskMod;
import net.mcreator.asterrisk.compat.CuriosCompat;
import net.mcreator.asterrisk.damage.ModDamageTypes;
import net.mcreator.asterrisk.registry.ModSounds;
import net.mcreator.asterrisk.util.SolarCombatHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 陽の化身 - Sun Incarnate
 * 太陽の力の顕現たる最上位ボス。
 *
 * 特性:
 * - HP4000 / 攻撃50+α（接触炎上） / 防御50+α
 * - 半径20ブロック内の生物を炎上させるオーラ
 * - setHealthによる体力操作無効（内部ダメージ処理のみ許可）
 * - ピースフル以外でのdiscard無効
 * - killコマンド（generic_kill）以外の即死系ダメージ無効
 * - 半径20ブロックより外からの攻撃無効
 * - 最大体力以上の一撃を受けた場合、ダメージを攻撃者へ反射（ブロック起因なら該当ブロックを空気化）
 * - 被弾後1秒間無敵。無敵を貫通されたダメージは解除時に100倍回復し、死亡判定も解除後まで保留
 * - 1回あたりの被ダメージ上限 =（攻撃者の防具エンチャントLv合計 - 武器エンチャントLv合計）× 100
 *   （下限10 / 上限は最大体力の半分）
 * - 炎上・爆発無効、接触ダメージ = 相手の防具エンチャントLv合計 × 100（なしなら1000）
 * - 半径20ブロック内の液体を消去、天候を晴れに固定
 * - 召喚時の最大体力を記録し、改変されたら復元して全回復
 * - NoAI / tickキャンセルによる行動停止を無効化
 * - 攻撃手段: 火炎弾・インファイト・隕石落下・爆破・突進・吸い込み
 * - 移動不能時や突進経路上のブロックを破壊
 */
public class SunIncarnateEntity extends Monster {

    /** オーラ・攻撃制限・液体消去の共通半径 */
    private static final double EFFECT_RADIUS = 20.0D;
    /** エンチャントなしの相手に対する接触ダメージ */
    private static final float CONTACT_DAMAGE_BASE = 1000.0F;
    /** エンチャントレベル1あたりの接触ダメージ / 被ダメージ上限の係数 */
    private static final float ENCHANT_SCALE = 100.0F;
    /** 被ダメージ上限の下限値 */
    private static final float MIN_DAMAGE_CAP = 10.0F;
    /** 被弾後の無敵時間（tick） */
    private static final int POST_HURT_INVULN = 20;

    /**
     * 貫通攻撃を受けている最中かどうか（Stellar Scepter等が設定）。
     * SynchedEntityData経由でクライアントにも同期し、演出にも利用する。
     */
    private static final EntityDataAccessor<Boolean> DATA_PIERCED =
        SynchedEntityData.defineId(SunIncarnateEntity.class, EntityDataSerializers.BOOLEAN);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
        Component.translatable("entity.aster_risk.sun_incarnate"),
        BossEvent.BossBarColor.YELLOW,
        BossEvent.BossBarOverlay.NOTCHED_20
    );

    // 体力操作ガード（内部のダメージ適用とNBT読み込みのみ体力減少を許可）
    private boolean allowHealthChange = false;
    // 即死反射の再帰ガード
    private boolean reflecting = false;

    private int postHurtInvulnTicks = 0;
    private int specialAttackTimer = 0;
    private int auraTimer = 0;
    private int liquidScanLayer = -(int) EFFECT_RADIUS;

    /** 召喚時に確定した最大体力（改変検知用、-1は未確定） */
    private float lockedMaxHealth = -1.0F;
    /** 無敵開始時点の体力（貫通ダメージ検知用） */
    private float healthAtInvulnStart = 0.0F;
    /** 無敵中に貫通ダメージを受けたか */
    private boolean penetratedDuringInvuln = false;
    /** 無敵中に保留された死亡ソース */
    @Nullable
    private DamageSource pendingDeathSource = null;

    // 突進
    private int chargeTicks = 0;
    private Vec3 chargeDirection = Vec3.ZERO;

    // 吸い込み
    private int suctionTicks = 0;

    // スタック検知
    private Vec3 lastPos = Vec3.ZERO;
    private int stuckTicks = 0;

    private int particleTimer = 0;

    public SunIncarnateEntity(EntityType<? extends SunIncarnateEntity> type, Level level) {
        super(type, level);
        this.xpReward = 1200;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 24.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PIERCED, false);
    }

    /**
     * 貫通攻撃属性の設定。
     * trueの間は被ダメージ上限と射程制限を無視してダメージが通る。
     */
    public void setPierced(boolean pierced) {
        this.entityData.set(DATA_PIERCED, pierced);
    }

    public boolean isPierced() {
        return this.entityData.get(DATA_PIERCED);
    }

    /**
     * 貫通攻撃によって削られた体力を正規のダメージとして受理する。
     * 無敵中の巻き戻し（および解除時の全回復）の対象から外すため、
     * 基準体力を現在値まで引き下げる。
     */
    public void acceptPiercingDamage() {
        this.healthAtInvulnStart = Math.min(this.healthAtInvulnStart, this.getHealth());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 4000.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.45D)
            .add(Attributes.ATTACK_DAMAGE, 50.0D)   // +α: 命中時の炎上（doHurtTarget）
            .add(Attributes.ARMOR, 50.0D)           // +α: ARMOR_TOUGHNESS
            .add(Attributes.ARMOR_TOUGHNESS, 15.0D)
            .add(Attributes.FOLLOW_RANGE, 64.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    // ===== 体力操作ガード（sethealth無効） =====

    @Override
    public void setHealth(float health) {
        // 内部処理以外による体力減少を拒否（回復・初期化は許可）
        if (allowHealthChange || health >= this.getHealth()) {
            super.setHealth(health);
        }
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        this.allowHealthChange = true;
        try {
            super.actuallyHurt(source, amount);
        } finally {
            this.allowHealthChange = false;
        }
    }

    // ===== discard制限（ピースフル以外で無効） =====

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (reason == Entity.RemovalReason.DISCARDED
                && this.level().getDifficulty() != Difficulty.PEACEFUL) {
            return;
        }
        // 無敵中の撃破による削除も拒否（解除時の回復処理より先に消えないようにする）
        if (reason == Entity.RemovalReason.KILLED && postHurtInvulnTicks > 0) {
            return;
        }
        super.remove(reason);
    }

    // ===== ダメージ制御 =====

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // killコマンド（generic_kill）は常に有効
        if (source.is(DamageTypes.GENERIC_KILL)) {
            return super.hurt(source, amount);
        }
        // その他の即死系（奈落など無敵貫通ダメージ）は無効
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        // 爆発・炎系無効
        if (source.is(DamageTypeTags.IS_EXPLOSION) || source.is(DamageTypeTags.IS_FIRE)) {
            return false;
        }
        // 被弾後1秒間は無敵
        if (postHurtInvulnTicks > 0) {
            return false;
        }
        // 貫通攻撃は射程制限を無視する
        if (!isPierced()) {
            // 半径20ブロックより外からの攻撃は無効
            Vec3 sourcePos = null;
            if (source.getEntity() != null) {
                sourcePos = source.getEntity().position();
            } else if (source.getSourcePosition() != null) {
                sourcePos = source.getSourcePosition();
            }
            if (sourcePos != null && this.position().distanceTo(sourcePos) > EFFECT_RADIUS) {
                return false;
            }
        }
        // 最大体力以上の一撃はダメージを攻撃者へ移し替える
        if (amount >= this.getMaxHealth()) {
            if (!reflecting) {
                reflecting = true;
                try {
                    reflectLethalDamage(source, amount);
                } finally {
                    reflecting = false;
                }
            }
            return false;
        }

        // 1回あたりの被ダメージ上限を適用（貫通攻撃は上限を無視）
        float capped = isPierced() ? amount : Math.min(amount, computeDamageCap(source));

        boolean result = super.hurt(source, capped);
        if (result) {
            postHurtInvulnTicks = POST_HURT_INVULN;
            healthAtInvulnStart = this.getHealth();
        }
        return result;
    }

    /**
     * 1回あたりの被ダメージ上限。
     * （攻撃者の防具エンチャントレベル合計 - 武器エンチャントレベル合計）× 100
     * 下限10 / エンチャントなしも10 / 上限は最大体力の半分。
     */
    private float computeDamageCap(DamageSource source) {
        float hardCap = this.getMaxHealth() / 2.0F;
        if (!(source.getEntity() instanceof LivingEntity attacker)) {
            return Math.min(MIN_DAMAGE_CAP, hardCap);
        }

        int armorLevels = sumEnchantLevels(attacker, true);
        int weaponLevels = sumEnchantLevels(attacker, false);
        if (armorLevels == 0 && weaponLevels == 0) {
            return Math.min(MIN_DAMAGE_CAP, hardCap);
        }

        float cap = (armorLevels - weaponLevels) * ENCHANT_SCALE;
        return Mth.clamp(cap, MIN_DAMAGE_CAP, hardCap);
    }

    /**
     * エンチャントレベルの合計を集計。
     * @param armorSlots true=防具4部位 / false=手持ち2スロット
     */
    private static int sumEnchantLevels(LivingEntity entity, boolean armorSlots) {
        int total = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.isArmor() != armorSlots) continue;
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            for (int level : EnchantmentHelper.getEnchantments(stack).values()) {
                total += level;
            }
        }
        return total;
    }

    // ===== 無敵中の死亡保留（4・5） =====

    @Override
    public void die(DamageSource source) {
        // 無敵中は死亡判定を保留（無敵解除時の回復処理後に再判定）
        if (postHurtInvulnTicks > 0) {
            pendingDeathSource = source;
            return;
        }
        super.die(source);
    }

    @Override
    public boolean isDeadOrDying() {
        // 無敵中は死亡状態とみなさない（死亡アニメーション・削除を抑止）
        if (postHurtInvulnTicks > 0) {
            return false;
        }
        return super.isDeadOrDying();
    }

    /**
     * 無敵解除時の処理。
     * 無敵を貫通して受けたダメージがあれば全回復してから死亡判定を行う。
     */
    private void onInvulnerabilityEnd() {
        boolean penetrated = penetratedDuringInvuln || this.getHealth() < healthAtInvulnStart;
        penetratedDuringInvuln = false;

        if (penetrated) {
            // 特殊ダメージ源による削り切りへの対抗として全回復
            // 最大体力自体が改変されている可能性があるので先に復元してから回復する
            enforceMaxHealth();
            // heal()は体力0のとき機能しないため直接設定（0からの復帰に対応）
            allowHealthChange = true;
            try {
                this.setHealth(this.getMaxHealth());
            } finally {
                allowHealthChange = false;
            }
            pendingDeathSource = null;

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.playSound(null, this.blockPosition(),
                    SoundEvents.RESPAWN_ANCHOR_CHARGE, this.getSoundSource(), 2.0F, 1.4F);
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    this.getX(), this.getY() + 2.0D, this.getZ(), 40, 1.5, 2.0, 1.5, 0.15);
            }
        }

        // 保留していた死亡判定をここで確定
        if (this.getHealth() <= 0.0F) {
            DamageSource source = pendingDeathSource != null
                ? pendingDeathSource : this.damageSources().generic();
            pendingDeathSource = null;
            super.die(source);
        } else {
            pendingDeathSource = null;
        }
    }

    // ===== 最大体力の固定（3） =====

    /**
     * 召喚時の最大体力を保存し、以降改変されていたら復元して全回復する。
     */
    private void enforceMaxHealth() {
        if (lockedMaxHealth <= 0.0F) {
            lockedMaxHealth = this.getMaxHealth();
            return;
        }
        if (this.getMaxHealth() == lockedMaxHealth) {
            return;
        }

        AttributeInstance attr = this.getAttribute(Attributes.MAX_HEALTH);
        if (attr != null) {
            for (AttributeModifier modifier : new ArrayList<>(attr.getModifiers())) {
                attr.removeModifier(modifier);
            }
            attr.setBaseValue(lockedMaxHealth);
        }

        // 改変への対抗として全回復
        allowHealthChange = true;
        try {
            this.setHealth(lockedMaxHealth);
        } finally {
            allowHealthChange = false;
        }
    }

    // ===== tick停止無効（6） =====

    @Override
    public void setNoAi(boolean noAi) {
        // NoAIによる行動停止を拒否
    }

    @Override
    public boolean isNoAi() {
        return false;
    }

    /**
     * 他modによるtickキャンセルを打ち消す
     */
    @Mod.EventBusSubscriber(modid = AsterRiskMod.MODID)
    public static class TickGuard {
        @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
        public static void onLivingTick(LivingEvent.LivingTickEvent event) {
            if (event.getEntity() instanceof SunIncarnateEntity && event.isCanceled()) {
                event.setCanceled(false);
            }
        }
    }

    /**
     * 即死級ダメージの反射。
     * 攻撃者がエンティティならそのままダメージを移し替え、全装備の耐久値を最大値の10%分減らす。
     * ブロック起因（攻撃エンティティなし・発生位置あり）なら該当ブロックを空気で上書きする。
     */
    private void reflectLethalDamage(DamageSource source, float amount) {
        Entity attacker = source.getEntity() != null ? source.getEntity() : source.getDirectEntity();
        if (attacker instanceof LivingEntity living && living != this) {
            degradeEquipment(living);
            living.hurt(this.damageSources().mobAttack(this), amount);
        } else if (attacker == null) {
            Vec3 pos = source.getSourcePosition();
            if (pos != null) {
                BlockPos blockPos = BlockPos.containing(pos);
                if (!this.level().getBlockState(blockPos).isAir()) {
                    this.level().setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        // 反射の演出
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.blockPosition(),
                SoundEvents.BLAZE_SHOOT, this.getSoundSource(), 2.0F, 0.6F);
        }
    }

    /**
     * 装備劣化: 全装備スロットの耐久値を最大値の10%分減らす。
     * 耐久値を持たない防具・武器は強制的に外してその場にドロップさせる。
     */
    private void degradeEquipment(LivingEntity target) {
        SolarCombatHelper.degradeEquipment(target, this);
    }

    // ===== メインループ =====

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide()) {
            spawnParticles();
            return;
        }

        // (3) 最大体力の改変を検知して復元
        enforceMaxHealth();

        // (4)(5) 無敵の消化と解除時処理（貫通ダメージの全回復＋保留した死亡判定）
        if (postHurtInvulnTicks > 0) {
            // 貫通されたダメージは即座に巻き戻す（無敵中の死亡・削除を防ぐ）
            if (this.getHealth() < healthAtInvulnStart) {
                penetratedDuringInvuln = true;
                allowHealthChange = true;
                try {
                    this.setHealth(healthAtInvulnStart);
                } finally {
                    allowHealthChange = false;
                }
            }
            postHurtInvulnTicks--;
            if (postHurtInvulnTicks == 0) {
                onInvulnerabilityEnd();
            }
        }

        specialAttackTimer++;
        auraTimer++;

        // (b) 半径20ブロックの生物を炎上 + 定期処理
        if (auraTimer >= 20) {
            auraTimer = 0;
            igniteAura();
            enforceClearWeather();
            updateDynamicStats();
        }

        // (f) 接触1000ダメージ
        contactDamage();

        // (f) 液体消去（1tickにつき1レイヤーずつ走査）
        purgeLiquidsLayer();

        // 突進・吸い込み処理
        if (chargeTicks > 0) {
            tickCharge();
        } else if (suctionTicks > 0) {
            tickSuction();
        } else {
            // (h) スタック検知 → 周囲のブロック破壊
            detectStuckAndBreak();

            // 特殊攻撃
            if (specialAttackTimer >= 60 && this.getTarget() != null) {
                specialAttackTimer = 0;
                performSpecialAttack();
            }
        }
    }

    /** スポーン中は天候を晴れに固定 */
    private void enforceClearWeather() {
        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel
                && (serverLevel.isRaining() || serverLevel.isThundering())) {
            serverLevel.setWeatherParameters(6000, 0, false, false);
        }
    }

    /**
     * ターゲットの武器・防具に付いたエンチャント数に応じて攻撃力/防御力を変動
     * （基礎50 + 10×エンチャント個数）
     */
    private void updateDynamicStats() {
        int enchantCount = 0;
        if (this.getTarget() instanceof Player player) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = player.getItemBySlot(slot);
                if (!stack.isEmpty()) {
                    enchantCount += EnchantmentHelper.getEnchantments(stack).size();
                }
            }
        }
        double bonus = enchantCount * 10.0D;
        var attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
            attack.setBaseValue(50.0D + bonus);
        }
        var armor = this.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.setBaseValue(50.0D + bonus);
        }
    }

    /** 半径20ブロック内の生物を炎上させる */
    private void igniteAura() {
        AABB area = this.getBoundingBox().inflate(EFFECT_RADIUS);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area,
            e -> e != this && !(e instanceof SunIncarnateEntity));
        for (LivingEntity target : targets) {
            target.setSecondsOnFire(5);
        }
    }

    /**
     * 接触ダメージ＋装備劣化。
     * ダメージ量は相手の防具エンチャントレベル合計×100（エンチャントなしは1000固定）。
     */
    private void contactDamage() {
        AABB touchArea = this.getBoundingBox().inflate(0.25D);
        List<LivingEntity> touching = this.level().getEntitiesOfClass(LivingEntity.class, touchArea,
            e -> e != this && !(e instanceof SunIncarnateEntity));
        for (LivingEntity target : touching) {
            int armorLevels = sumEnchantLevels(target, true);
            float damage = armorLevels > 0 ? armorLevels * ENCHANT_SCALE : CONTACT_DAMAGE_BASE;

            // 防具・耐性・エンチャント保護を貫通する専用ダメージ源
            DamageSource source = ModDamageTypes.solarContact(this.level(), this);
            if (source == null) {
                source = this.damageSources().mobAttack(this);
            }

            target.hurt(source, damage);
            degradeEquipment(target);
            target.setSecondsOnFire(8);
        }
    }

    /** 半径20ブロック内の液体を1レイヤーずつ消去（球状判定） */
    private void purgeLiquidsLayer() {
        int r = (int) EFFECT_RADIUS;
        int dy = liquidScanLayer;
        liquidScanLayer++;
        if (liquidScanLayer > r) {
            liquidScanLayer = -r;
        }

        BlockPos center = this.blockPosition();
        int y = center.getY() + dy;
        if (y < this.level().getMinBuildHeight() || y > this.level().getMaxBuildHeight()) {
            return;
        }

        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                if (dx * dx + dy * dy + dz * dz > r * r) continue;
                BlockPos pos = new BlockPos(center.getX() + dx, y, center.getZ() + dz);
                BlockState state = this.level().getBlockState(pos);
                if (state.getFluidState().isEmpty()) continue;

                if (state.getBlock() instanceof LiquidBlock) {
                    this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                } else if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    this.level().setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
                } else {
                    // 昆布・海草など液体内包ブロック
                    this.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    // ===== 特殊攻撃 =====

    private void performSpecialAttack() {
        LivingEntity target = this.getTarget();

        // ターゲットが3ブロック以上高い位置にいる場合は吸い込みを積極的に使用
        if (target != null && target.getY() - this.getY() >= 3.0D) {
            startSuction();
            return;
        }

        int attackType = this.random.nextInt(5);
        switch (attackType) {
            case 0 -> fireballVolley();
            case 1 -> meteorFall();
            case 2 -> explosionBlast();
            case 3 -> startCharge();
            case 4 -> startSuction();
        }
    }

    /** 吸い込み開始 */
    private void startSuction() {
        suctionTicks = 20;
        this.level().playSound(null, this.blockPosition(),
            SoundEvents.ENDER_DRAGON_GROWL, this.getSoundSource(), 2.0F, 0.5F);
    }

    /** 吸い込み中の処理: 周囲の生物を高速で引き寄せ、終了後は突進に移行 */
    private void tickSuction() {
        suctionTicks--;

        AABB area = this.getBoundingBox().inflate(EFFECT_RADIUS);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area,
            e -> e != this && !(e instanceof SunIncarnateEntity));
        Vec3 center = this.position().add(0, 1.0D, 0);

        for (LivingEntity entity : targets) {
            Vec3 pull = center.subtract(entity.position());
            double dist = pull.length();
            if (dist < 2.0D) continue; // 至近距離（接触ダメージ圏）はそれ以上引かない

            entity.setDeltaMovement(pull.normalize().scale(1.2D));
            entity.hurtMarked = true; // 速度変更をクライアントに同期
        }

        // 渦のパーティクル
        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            for (int i = 0; i < 6; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2;
                double radius = 4.0D + this.random.nextDouble() * 12.0D;
                serverLevel.sendParticles(ParticleTypes.FLAME,
                    this.getX() + Math.cos(angle) * radius,
                    this.getY() + 1.0D + this.random.nextDouble() * 3.0D,
                    this.getZ() + Math.sin(angle) * radius,
                    0, -Math.cos(angle) * 0.5D, 0.0D, -Math.sin(angle) * 0.5D, 0.5D);
            }
        }

        // 吸い込み終了後は突進で追撃（固定）
        if (suctionTicks <= 0) {
            startCharge();
        }
    }

    /** 火炎弾: ターゲットへ3連小火炎弾 */
    private void fireballVolley() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        Vec3 dir = target.getEyePosition().subtract(this.getEyePosition());
        for (int i = 0; i < 3; i++) {
            double spreadX = (this.random.nextDouble() - 0.5D) * 0.6D;
            double spreadZ = (this.random.nextDouble() - 0.5D) * 0.6D;
            SmallFireball fireball = new SmallFireball(this.level(), this,
                dir.x + spreadX, dir.y, dir.z + spreadZ);
            fireball.setPos(this.getX(), this.getEyeY(), this.getZ());
            this.level().addFreshEntity(fireball);
        }
        this.level().playSound(null, this.blockPosition(),
            SoundEvents.BLAZE_SHOOT, this.getSoundSource(), 1.5F, 1.0F);
    }

    /** 隕石落下: ターゲット周辺の上空から大火球を降らせる */
    private void meteorFall() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        for (int i = 0; i < 4; i++) {
            double offsetX = (this.random.nextDouble() - 0.5D) * 12.0D;
            double offsetZ = (this.random.nextDouble() - 0.5D) * 12.0D;
            LargeFireball meteor = new LargeFireball(this.level(), this, 0.0D, -1.0D, 0.0D, 2);
            meteor.setPos(target.getX() + offsetX, target.getY() + 15.0D, target.getZ() + offsetZ);
            this.level().addFreshEntity(meteor);
        }
        this.level().playSound(null, this.blockPosition(),
            SoundEvents.GHAST_SHOOT, this.getSoundSource(), 2.0F, 0.7F);
    }

    /** 爆破: ターゲット位置で爆発（mobGriefingルールに従う） */
    private void explosionBlast() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        this.level().explode(this, target.getX(), target.getY(), target.getZ(),
            3.0F, Level.ExplosionInteraction.MOB);
    }

    /** 突進開始 */
    private void startCharge() {
        LivingEntity target = this.getTarget();
        if (target == null) return;

        Vec3 dir = target.position().subtract(this.position());
        Vec3 horizontal = new Vec3(dir.x, 0, dir.z);
        if (horizontal.lengthSqr() < 0.01D) return;

        this.chargeDirection = horizontal.normalize();
        this.chargeTicks = 20;
        this.level().playSound(null, this.blockPosition(),
            SoundEvents.RAVAGER_ROAR, this.getSoundSource(), 2.0F, 1.2F);
    }

    /** 突進中の処理: 高速移動・経路上のブロック破壊・接触ダメージ */
    private void tickCharge() {
        chargeTicks--;

        this.setDeltaMovement(chargeDirection.x * 1.2D, this.getDeltaMovement().y, chargeDirection.z * 1.2D);
        this.setYRot((float) (Math.atan2(chargeDirection.z, chargeDirection.x) * 180.0D / Math.PI) - 90.0F);

        // (h) 突進経路上のブロックを消去
        AABB path = this.getBoundingBox()
            .expandTowards(chargeDirection.x * 2.0D, 0.5D, chargeDirection.z * 2.0D);
        breakBlocksIn(path);

        // 経路上のエンティティへダメージ
        List<LivingEntity> hit = this.level().getEntitiesOfClass(LivingEntity.class,
            this.getBoundingBox().inflate(1.0D),
            e -> e != this && !(e instanceof SunIncarnateEntity));
        for (LivingEntity entity : hit) {
            entity.hurt(this.damageSources().mobAttack(this), 60.0F);
            Vec3 knockback = entity.position().subtract(this.position()).normalize().scale(2.0D);
            entity.setDeltaMovement(knockback.x, 0.6D, knockback.z);
            entity.setSecondsOnFire(6);
        }
    }

    /** スタック検知: 2秒間ほぼ動けない場合、周囲のブロックを破壊 */
    private void detectStuckAndBreak() {
        if (this.getTarget() == null) {
            stuckTicks = 0;
            lastPos = this.position();
            return;
        }

        if (this.position().distanceToSqr(lastPos) < 0.01D) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }
        lastPos = this.position();

        if (stuckTicks >= 40) {
            stuckTicks = 0;
            breakBlocksIn(this.getBoundingBox().inflate(1.0D, 0.5D, 1.0D));
        }
    }

    /** 範囲内の破壊可能ブロックを消去（岩盤など破壊不能ブロックは除外） */
    private void breakBlocksIn(AABB area) {
        BlockPos min = BlockPos.containing(area.minX, area.minY, area.minZ);
        BlockPos max = BlockPos.containing(area.maxX, area.maxY, area.maxZ);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = this.level().getBlockState(pos);
            if (!state.isAir()
                    && state.getFluidState().isEmpty()
                    && state.getDestroySpeed(this.level(), pos) >= 0) {
                this.level().destroyBlock(pos.immutable(), false, this);
            }
        }
    }

    // ===== 戦闘その他 =====

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean result = super.doHurtTarget(target);
        // +α: 命中時に炎上
        if (result && target instanceof LivingEntity living) {
            living.setSecondsOnFire(8);
        }
        return result;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    // ===== 演出 =====

    private void spawnParticles() {
        particleTimer++;
        if (particleTimer < 2) return;
        particleTimer = 0;

        // 体を覆う炎
        this.level().addParticle(
            ParticleTypes.FLAME,
            this.getRandomX(1.2D), this.getRandomY(), this.getRandomZ(1.2D),
            0, 0.03D, 0
        );
        // 火の粉
        if (this.random.nextInt(3) == 0) {
            this.level().addParticle(
                ParticleTypes.LAVA,
                this.getRandomX(0.8D), this.getY() + 1.0D, this.getRandomZ(0.8D),
                0, 0, 0
            );
        }
        // 突進中は炎の軌跡
        if (chargeTicks > 0) {
            for (int i = 0; i < 5; i++) {
                this.level().addParticle(
                    ParticleTypes.FLAME,
                    this.getRandomX(1.5D), this.getRandomY(), this.getRandomZ(1.5D),
                    -chargeDirection.x * 0.3D, 0.05D, -chargeDirection.z * 0.3D
                );
            }
        }
    }

    public boolean isCharging() {
        return chargeTicks > 0;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BOSS_ATTACK.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BOSS_DEATH.get();
    }

    // ===== ボスバー・永続化 =====

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("ChargeTicks", this.chargeTicks);
        tag.putInt("SuctionTicks", this.suctionTicks);
        tag.putFloat("LockedMaxHealth", this.lockedMaxHealth);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        // NBTからの体力復元を許可
        this.allowHealthChange = true;
        try {
            super.readAdditionalSaveData(tag);
        } finally {
            this.allowHealthChange = false;
        }
        if (tag.contains("ChargeTicks")) {
            this.chargeTicks = tag.getInt("ChargeTicks");
        }
        if (tag.contains("SuctionTicks")) {
            this.suctionTicks = tag.getInt("SuctionTicks");
        }
        if (tag.contains("LockedMaxHealth")) {
            this.lockedMaxHealth = tag.getFloat("LockedMaxHealth");
        }
    }
}
