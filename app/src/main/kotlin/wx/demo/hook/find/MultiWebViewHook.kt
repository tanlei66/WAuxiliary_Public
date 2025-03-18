package wx.demo.hook.find

import android.content.Intent
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
object MultiWebViewHook : SwitchHook("MultiWebViewHook"), IDexFind {
    private object MethodStart : DescriptorData("MultiWebViewHook.MethodStart")

    override val location = "增强"
    override val funcName = "文章多窗"
    override val funcDesc = "可同时阅读多篇公众号文章在多窗口中"

    override fun initOnce() {
        MethodStart.desc.toDexMethod().hook {
            beforeIfEnabled {
                if (args(2).string() == ".ui.timeline.preload.ui.TmplWebViewMMUI") {
                    val intent = args(3).cast<Intent>()!!
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                }
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodStart.desc = dexKit.findMethod {
            matcher {
                usingEqStrings("MicroMsg.PluginHelper", "start multi webview!!!!!!!!!")
            }
        }.single().descriptor
    }
}
