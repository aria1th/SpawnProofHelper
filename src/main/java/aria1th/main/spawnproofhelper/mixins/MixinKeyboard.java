package aria1th.main.spawnproofhelper.mixins;

import aria1th.main.spawnproofhelper.utils.SpawnProofLocation;
import net.minecraft.client.Keyboard;
import net.minecraft.client.option.GameOptions;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {
	@Inject(method = "onKey", at = @At("HEAD"))
	private void onKeyAction(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci){
		if (key == GLFW.GLFW_KEY_APOSTROPHE && action == 0){
			SpawnProofLocation.switchOnOff();
		}
		else if (key == GLFW.GLFW_KEY_ESCAPE){
			SpawnProofLocation.refreshInstance();
		}
	}
}