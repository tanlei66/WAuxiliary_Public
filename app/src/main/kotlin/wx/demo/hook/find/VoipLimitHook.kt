package wx.demo.hook.find

import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import me.hd.wauxv.data.config.DescriptorData
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.api.IDexFind
import me.hd.wauxv.hook.base.SwitchHook
import me.hd.wauxv.hook.factory.toDexMethod
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
@HookAnno
@ViewAnno
object VoipLimitHook : SwitchHook("VoipLimitHook"), IDexFind {
    private object MethodIsVoiceUsing : DescriptorData("VoipLimitHook.MethodIsVoiceUsing")
    private object MethodIsMultiTalking : DescriptorData("VoipLimitHook.MethodIsMultiTalking")
    private object MethodMarkCheckAppBrand : DescriptorData("VoipLimitHook.MethodMarkCheckAppBrand")
    private object MethodIsCameraUsing : DescriptorData("VoipLimitHook.MethodIsCameraUsing")

    override val location = "增强"
    override val funcName = "通话限制"
    override val funcDesc = "将通话中无法播放及拍摄视频限制移除"

    override fun initOnce() {
        listOf(
            MethodIsVoiceUsing.desc.toDexMethod(),
            MethodIsMultiTalking.desc.toDexMethod(),
            MethodMarkCheckAppBrand.desc.toDexMethod(),
            MethodIsCameraUsing.desc.toDexMethod(),
        ).forEach { method ->
            method.hook {
                beforeIfEnabled {
                    resultFalse()
                }
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodIsVoiceUsing.desc = dexKit.findMethod {
            matcher {
                paramTypes(ContextClass)
                usingEqStrings("MicroMsg.DeviceOccupy", "isVoiceUsing")
            }
        }.single().descriptor
        MethodIsMultiTalking.desc = dexKit.findMethod {
            matcher {
                paramTypes(ContextClass)
                usingEqStrings("MicroMsg.DeviceOccupy", "isMultiTalking")
            }
        }.single().descriptor
        MethodMarkCheckAppBrand.desc = dexKit.findMethod {
            matcher {
                paramTypes(ContextClass)
                usingEqStrings("MicroMsg.DeviceOccupy", "checkAppBrandVoiceUsingAndShowToast isVoiceUsing:%b, isCameraUsing:%b")
            }
        }.single().descriptor
        MethodIsCameraUsing.desc = dexKit.findMethod {
            matcher {
                paramTypes(ContextClass, BooleanType, BundleClass)
                usingEqStrings("MicroMsg.DeviceOccupy", "isCameraUsing")
            }
        }.single().descriptor
    }
}
