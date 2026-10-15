package net.leaves.ertclanlist.mixin;

import net.leaves.ertclanlist.ClanListManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ExampleMixin {

	private boolean ertclanlist$shouldHighlight() {
		Entity self = (Entity) (Object) this;
		if (!self.level().isClientSide()) return false;
		if (!(self instanceof Player player)) return false;
		return ClanListManager.contains(player.getName().getString());
	}

	@Inject(method = "isCurrentlyGlowing", at = @At("RETURN"), cancellable = true)
	private void ertclanlist$glow(CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValueZ() && ertclanlist$shouldHighlight()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
	private void ertclanlist$color(CallbackInfoReturnable<Integer> cir) {
		if (ertclanlist$shouldHighlight()) {
			cir.setReturnValue(ClanListManager.getColor());
		}
	}
}