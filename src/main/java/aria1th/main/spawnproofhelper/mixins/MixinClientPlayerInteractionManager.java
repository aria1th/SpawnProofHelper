package aria1th.main.spawnproofhelper.mixins;

import aria1th.main.spawnproofhelper.utils.SpawnProofLocation;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
    @Inject(at = @At("HEAD"), method = "tick")
    private void init(CallbackInfo ci) {
        if (SpawnProofLocation.isEnabled()) {
            SpawnProofLocation.tick();
        }
    }
}