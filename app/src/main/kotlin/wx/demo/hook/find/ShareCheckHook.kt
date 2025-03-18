package wx.demo.hook.find

import me.hd.wauxv.data.config.DescriptorData
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.api.IDexFind
import me.hd.wauxv.hook.base.SwitchHook
import me.hd.wauxv.hook.factory.toDexMethod
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

/**
 * Author: https://github.com/Keeperorowner
 */
@Obfuscate
@HookAnno
@ViewAnno
object ShareCheckHook : SwitchHook("ShareSignatureHook"), IDexFind {
    private object MethodCheckSign : DescriptorData("ShareSignatureHook.MethodCheckSign")

    override val location = "增强"
    override val funcName = "分享校验"
    override val funcDesc = "绕过第三方应用分享到微信的签名校验"

    override fun initOnce() {
        MethodCheckSign.desc.toDexMethod().hook {
            beforeIfEnabled {
                resultTrue()
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodCheckSign.desc = dexKit.findMethod {
            matcher {
                usingEqStrings("checkAppSignature get local signature failed")
            }
        }.single().descriptor
    }
}
