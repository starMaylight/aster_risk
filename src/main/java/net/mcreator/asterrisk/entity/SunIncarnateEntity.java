package net.mcreator.asterrisk.entity;

import net.mcreator.asterrisk.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
 * - 被弾後1秒間無敵
 * - 炎上・爆発無効、接触で1000ダメージ、半径20ブロック内の液体を消去
 * - 攻撃手段: 火炎弾・インファイト・隕石落下・爆破・突進
 * - 移動不能時や突進経路上のブロックを破壊
 */
public class SunIncarnateEntity extends Monster {

    /** オーラ・攻撃制限・液体消去の共通半径 */
    private static final double EFFECT_RADIUS = 20.0D;
    /** 接触ダメージ */
    private static final float CONTACT_DAMAGE = 1000.0F;
    /** 被弾後の無敵時間（tick） */
    private static final int POST_HURT_INVULN = 20;

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

    // 突進
    private int chargeTicks = 0;
    private Vec3 chargeDirection = Vec3.ZERO;

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

        boolean result = super.hurt(source, amount);
        if (result) {
            postHurtInvulnTicks = POST_HURT_INVULN;
        }
        return result;
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
     * 耐久値を持たない防具・武器（Unbreakable含む）は太陽の熱で焼失する。
     */
    private void degradeEquipment(LivingEntity target) {
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return;
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = target.getItemBySlot(slot);
            if (stack.isEmpty()) continue;

            if (stack.isDamageableItem()) {
                int loss = Math.max(1, stack.getMaxDamage() / 10);
                stack.hurtAndBreak(loss, target, e -> e.broadcastBreakEvent(slot));
            } else if (isArmorOrWeapon(stack, slot)) {
                // 耐久値を持たない防具・武器は焼失
                target.setItemSlot(slot, ItemStack.EMPTY);
                this.level().playSound(null, target.blockPosition(),
                    SoundEvents.ITEM_BREAK, this.getSoundSource(), 1.0F, 0.7F);
            }
        }
    }

    /** 焼失対象（防具または武器）かどうかの判定 */
    private boolean isArmorOrWeapon(ItemStack stack, EquipmentSlot slot) {
        // 防具スロットに装備している物は防具とみなす
        if (slot.isArmor()) {
            return true;
        }
        Item item = stack.getItem();
        if (item instanceof SwordItem || item instanceof DiggerItem
                || item instanceof TridentItem || item instanceof ProjectileWeaponItem) {
            return true;
        }
        // 攻撃力修飾子を持つアイテムも武器とみなす
        return !stack.getAttributeModifiers(slot).get(Attributes.ATTACK_DAMAGE).isEmpty();
    }

    // ===== メインループ =====

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide()) {
            spawnParticles();
            return;
        }

        postHurtInvulnTicks = Math.max(0, postHurtInvulnTicks - 1);
        specialAttackTimer++;
        auraTimer++;

        // (b) 半径20ブロックの生物を炎上
        if (auraTimer >= 20) {
            auraTimer = 0;
            igniteAura();
        }

        // (f) 接触1000ダメージ
        contactDamage();

        // (f) 液体消去（1tickにつき1レイヤーずつ走査）
        purgeLiquidsLayer();

        // 突進処理
        if (chargeTicks > 0) {
            tickCharge();
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

    /** 半径20ブロック内の生物を炎上させる */
    private void igniteAura() {
        AABB area = this.getBoundingBox().inflate(EFFECT_RADIUS);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area,
            e -> e != this && !(e instanceof SunIncarnateEntity));
        for (LivingEntity target : targets) {
            target.setSecondsOnFire(5);
        }
    }

    /** 接触した生物に1000ダメージ＋装備劣化 */
    private void contactDamage() {
        AABB touchArea = this.getBoundingBox().inflate(0.25D);
        List<LivingEntity> touching = this.level().getEntitiesOfClass(LivingEntity.class, touchArea,
            e -> e != this && !(e instanceof SunIncarnateEntity));
        for (LivingEntity target : touching) {
            // ダメージが実際に通ったときのみ劣化（バニラの被弾クールダウンでレート制限）
            if (target.hurt(this.damageSources().mobAttack(this), CONTACT_DAMAGE)) {
                degradeEquipment(target);
            }
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
        int attackType = this.random.nextInt(4);
        switch (attackType) {
            case 0 -> fireballVolley();
            case 1 -> meteorFall();
            case 2 -> explosionBlast();
            case 3 -> startCharge();
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
    }
}
