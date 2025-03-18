package wx.demo.hook.find

import me.hd.wauxv.data.config.DescriptorData
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.api.IDexFind
import me.hd.wauxv.hook.base.SwitchHook
import me.hd.wauxv.hook.factory.toDexConstructor
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
@HookAnno
@ViewAnno
object MsgTailHook : SwitchHook("MsgTailHook"), IDexFind {
    private object MethodSendTextComponent : DescriptorData("MsgTailHook.MethodSendTextComponent")

    override val location = "美化"
    override val funcName = "消息尾巴"
    override val funcDesc = "设置聊天气泡消息尾部的显示样式效果"

    override fun initOnce() {
        MethodSendTextComponent.desc.toDexConstructor().hook {
            beforeIfEnabled {
                args(8).set(args(8).string() + "喵~")
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodSendTextComponent.desc = dexKit.findClass {
            matcher {
                usingEqStrings("MicroMsg.ChattingUI.SendTextComponent", "doSendMessage begin send txt msg")
            }
        }.single().findMethod {
            matcher {
                paramCount = 13
            }
        }.single().descriptor
    }
}
