package wx.demo.hook.ver

import android.view.LayoutInflater
import android.view.View
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.type.java.IntType
import me.hd.wauxv.data.config.DefaultData
import me.hd.wauxv.data.config.DescriptorData
import me.hd.wauxv.data.factory.HostInfo
import me.hd.wauxv.data.factory.WxVersion
import me.hd.wauxv.databinding.ModuleDialogVoiceLengthBinding
import me.hd.wauxv.factory.showDialog
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
object VoiceLengthHook : SwitchHook("VoiceLengthHook"), IDexFind {
    private object MethodSetVoice : DescriptorData("VoiceLengthHook.MethodSetVoice")
    private object ValVoiceLength : DefaultData("VoiceLengthHook.ValVoiceLength", intDefVal = 1)

    override val location = "辅助"
    override val funcName = "语音时长"
    override val funcDesc = "可自定义修改发送的语音消息显示时长"
    override var onClick: ((View) -> Unit)? = { layoutView ->
        val binding = ModuleDialogVoiceLengthBinding.inflate(LayoutInflater.from(layoutView.context))
        binding.moduleDialogSliderVoiceLength.value = ValVoiceLength.intVal.toFloat()
        layoutView.context.showDialog {
            title = funcName
            view = binding.root
            positiveButton("保存") {
                ValVoiceLength.intVal = binding.moduleDialogSliderVoiceLength.value.toInt()
            }
            negativeButton()
        }
    }
    override val isAvailable = HostInfo.versionCode > WxVersion.V8_0_30.code

    override fun initOnce() {
        MethodSetVoice.desc.toDexMethod().hook {
            beforeIfEnabled {
                val obj = args(0).any()!!
                val voiceLengthFieldName = "l"
                obj::class.java.field {
                    name = voiceLengthFieldName
                    type = IntType
                }.get(obj).set(ValVoiceLength.intVal * 1000)
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodSetVoice.desc = dexKit.findMethod {
            matcher {
                usingEqStrings("MicroMsg.VoiceStorage", "update failed, no values set")
            }
        }.single().descriptor
    }
}
