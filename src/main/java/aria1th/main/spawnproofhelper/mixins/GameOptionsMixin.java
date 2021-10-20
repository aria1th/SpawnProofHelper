package aria1th.main.spawnproofhelper.mixins;

import aria1th.main.spawnproofhelper.IGameOptions;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin implements IGameOptions {

    @Shadow @Final @Mutable
    public KeyBinding[] keysAll;

    public KeyBinding keySpawnProofHelperMenu;

    @Inject(
            method = "load",
            at = @At(
                    value = "HEAD"
            )
    )
    private void onLoadInjectAtHead(CallbackInfo ci) {
        keySpawnProofHelperMenu = new KeyBinding("Open SpawnProofHelper's menu", GLFW.GLFW_KEY_F7, "SpawnProofHelper");
        keysAll = ArrayUtils.add(keysAll, keySpawnProofHelperMenu);
    }

    @Override
    public KeyBinding getSpawnProofHelperMenu() {
        return keySpawnProofHelperMenu;
    }
}