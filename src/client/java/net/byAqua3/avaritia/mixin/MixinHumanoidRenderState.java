package net.byAqua3.avaritia.mixin;

import net.byAqua3.avaritia.render.layer.InfinityArmorState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public abstract class MixinHumanoidRenderState implements InfinityArmorState {
	@Unique private boolean avaritia$flying;
	@Unique private float avaritia$yaw;
	@Unique private float avaritia$pitch;

	@Override
	public boolean avaritia$isFlying() {
		return avaritia$flying;
	}

	@Override
	public void avaritia$setFlying(boolean flying) {
		avaritia$flying = flying;
	}

	@Override
	public float avaritia$getYaw() {
		return avaritia$yaw;
	}

	@Override
	public float avaritia$getPitch() {
		return avaritia$pitch;
	}

	@Override
	public void avaritia$setView(float yaw, float pitch) {
		avaritia$yaw = yaw;
		avaritia$pitch = pitch;
	}
}
