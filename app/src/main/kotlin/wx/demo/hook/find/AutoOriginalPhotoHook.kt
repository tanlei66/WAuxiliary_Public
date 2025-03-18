package wx.demo.hook.find

import android.app.Activity
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.base.SwitchHook
import me.hd.wauxv.hook.factory.toAppClass
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
@HookAnno
@ViewAnno
object AutoOriginalPhotoHook : SwitchHook("AutoOriginalPhotoHook") {
    override val location = "增强"
    override val funcName = "自动原图"
    override val funcDesc = "在发送图片和视频时自动勾选原图选项"

    override fun initOnce() {
        listOf(
            "com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI".toAppClass(),
            "com.tencent.mm.plugin.gallery.ui.ImagePreviewUI".toAppClass(),
        ).forEach { clazz ->
            clazz.method {
                name = "onCreate"
                param(BundleClass)
            }.hook {
                beforeIfEnabled {
                    val activity = instance<Activity>()
                    activity.intent.putExtra("send_raw_img", true)
                }
            }
        }
    }
}
