package wx.demo.hook.find

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
object AntiRevoke1Hook : SwitchHook("AntiRevoke1Hook"), IDexFind {
    private object MethodDoRevokeMsg : DescriptorData("AntiRevoke1Hook.MethodDoRevokeMsg")

    override val location = "增强"
    override val funcName = "阻止撤回1"
    override val funcDesc = "无撤回提示,长时间稳定流畅运行,2选1"

    override fun initOnce() {
        MethodDoRevokeMsg.desc.toDexMethod().hook {
            beforeIfEnabled {
                resultNull()
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodDoRevokeMsg.desc = dexKit.findMethod {
            matcher {
                usingEqStrings("doRevokeMsg xmlSrvMsgId=%d talker=%s isGet=%s")
            }
        }.single().descriptor
    }
}
