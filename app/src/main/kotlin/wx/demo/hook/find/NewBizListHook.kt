package wx.demo.hook.find

import android.content.ComponentName
import android.content.Intent
import com.highcapable.yukihookapi.hook.param.HookParam
import me.hd.wauxv.data.factory.PackageName
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.api.IStartActivity
import me.hd.wauxv.hook.base.SwitchHook
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
@HookAnno
@ViewAnno
object NewBizListHook : SwitchHook("NewBizListHook"), IStartActivity {
    override val location = "增强"
    override val funcName = "订阅列表"
    override val funcDesc = "订阅号消息从瀑布流模式改为列表模式"

    override fun initOnce() {
    }

    override fun onStartActivityIntent(param: HookParam, intent: Intent) {
        if (!isEnabled) return
        val bizFlutterView = "com.tencent.mm.plugin.brandservice.ui.flutter.BizFlutterTLFlutterViewActivity"
        val bizTimeLine = "com.tencent.mm.plugin.brandservice.ui.timeline.BizTimeLineUI"
        when (intent.component?.className) {
            bizFlutterView, bizTimeLine -> {
                intent.component = ComponentName(PackageName.WECHAT, "com.tencent.mm.ui.conversation.NewBizConversationUI")
            }
        }
    }
}
