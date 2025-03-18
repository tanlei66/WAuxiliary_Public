package wx.demo.hook.find

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.android.ButtonClass
import me.hd.wauxv.data.config.DefaultData
import me.hd.wauxv.databinding.ModuleDialogAutoLoginWinBinding
import me.hd.wauxv.factory.showDialog
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.SwitchHook
import me.hd.wauxv.hook.factory.toAppClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
@HookAnno
@ViewAnno
object AutoLoginWinHook : SwitchHook("AutoLoginWinHook") {
    private object ValAutoSyncMsg : DefaultData("AutoLoginWinHook.ValAutoSyncMsg", booleanDefVal = true)
    private object ValShowLoginDevice : DefaultData("AutoLoginWinHook.ValShowLoginDevice", booleanDefVal = true)
    private object ValAutoLoginDevice : DefaultData("AutoLoginWinHook.ValAutoLoginDevice", booleanDefVal = false)

    private const val AUTO_SYNC_MSG = 0b001   // 同步最近消息
    private const val SHOW_LOGIN_DEVICE = 0b010 // 显示登录设备
    private const val AUTO_LOGIN_DEVICE = 0b100 // 自动登录设备

    override val location = "增强"
    override val funcName = "自动登录"
    override val funcDesc = "微信请求登录时自动勾选项及点击按钮"
    override var onClick: ((View) -> Unit)? = { layoutView ->
        val binding = ModuleDialogAutoLoginWinBinding.inflate(LayoutInflater.from(layoutView.context))
        binding.moduleDialogCbAutoLoginWinAutoSyncMsg.isChecked = ValAutoSyncMsg.booleanVal
        binding.moduleDialogCbAutoLoginWinShowLoginDevice.isChecked = ValShowLoginDevice.booleanVal
        binding.moduleDialogCbAutoLoginWinAutoLoginDevice.isChecked = ValAutoLoginDevice.booleanVal
        layoutView.context.showDialog {
            title = funcName
            view = binding.root
            positiveButton("保存") {
                ValAutoSyncMsg.booleanVal = binding.moduleDialogCbAutoLoginWinAutoSyncMsg.isChecked
                ValShowLoginDevice.booleanVal = binding.moduleDialogCbAutoLoginWinShowLoginDevice.isChecked
                ValAutoLoginDevice.booleanVal = binding.moduleDialogCbAutoLoginWinAutoLoginDevice.isChecked
            }
            negativeButton()
        }
    }

    override fun initOnce() {
        "com.tencent.mm.plugin.webwx.ui.ExtDeviceWXLoginUI".toAppClass().apply {
            method {
                name = "onCreate"
                param(BundleClass)
            }.hook {
                beforeIfEnabled {
                    val activity = instance<Activity>()
                    var functionControl = 0
                    if (ValAutoSyncMsg.booleanVal) functionControl = functionControl or AUTO_SYNC_MSG
                    if (ValShowLoginDevice.booleanVal) functionControl = functionControl or SHOW_LOGIN_DEVICE
                    if (ValAutoLoginDevice.booleanVal) functionControl = functionControl or AUTO_LOGIN_DEVICE
                    activity.intent.putExtra("intent.key.function.control", functionControl)
                }
            }
            method {
                name = "initView"
                emptyParam()
            }.hook {
                afterIfEnabled {
                    val button = field {
                        type = ButtonClass
                    }.get(instance).cast<Button>()!!
                    button.callOnClick()
                }
            }
        }
    }
}
