package wx.demo.hook.find

import android.view.LayoutInflater
import android.view.View
import com.highcapable.yukihookapi.hook.type.android.ImageViewClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import me.hd.wauxv.data.config.DefaultData
import me.hd.wauxv.data.config.DescriptorData
import me.hd.wauxv.databinding.ModuleDialogRoundAvatarBinding
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
object RoundAvatarHook : SwitchHook("RoundAvatarHook"), IDexFind {
    private object MethodDrawable : DescriptorData("RoundAvatarHook.MethodDrawable")
    private object ValRoundAvatar : DefaultData("RoundAvatarHook.ValRoundAvatar", floatDefVal = 0.5f)

    override val location = "美化"
    override val funcName = "圆形头像"
    override val funcDesc = "可自定义微信全局头像渲染的圆形弧度"
    override var onClick: ((View) -> Unit)? = { layoutView ->
        val binding = ModuleDialogRoundAvatarBinding.inflate(LayoutInflater.from(layoutView.context))
        binding.moduleDialogSliderRoundAvatar.value = ValRoundAvatar.floatVal
        layoutView.context.showDialog {
            title = funcName
            view = binding.root
            positiveButton("保存") {
                ValRoundAvatar.floatVal = binding.moduleDialogSliderRoundAvatar.value
            }
            negativeButton()
        }
    }

    override fun initOnce() {
        MethodDrawable.desc.toDexMethod().hook {
            beforeIfEnabled {
                args(2).set(ValRoundAvatar.floatVal)
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodDrawable.desc = dexKit.findMethod {
            matcher {
                paramTypes(ImageViewClass, StringClass, FloatType, BooleanType)
                usingEqStrings("MicroMsg.AvatarDrawable")
            }
        }.single().descriptor
    }
}
