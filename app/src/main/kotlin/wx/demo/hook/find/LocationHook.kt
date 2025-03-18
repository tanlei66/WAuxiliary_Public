package wx.demo.hook.find

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.StringClass
import me.hd.wauxv.data.config.DefaultData
import me.hd.wauxv.data.config.DescriptorData
import me.hd.wauxv.data.factory.WxProcess
import me.hd.wauxv.databinding.ModuleDialogLocationBinding
import me.hd.wauxv.factory.showDialog
import me.hd.wauxv.hook.anno.HookAnno
import me.hd.wauxv.hook.anno.ViewAnno
import me.hd.wauxv.hook.api.IDexFind
import me.hd.wauxv.hook.base.SwitchHook
import me.hd.wauxv.hook.factory.toDexMethod
import me.hd.wauxv.hook.factory.toLazyAppClass
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
@HookAnno
@ViewAnno
object LocationHook : SwitchHook("LocationHook"), IDexFind {
    private object MethodListener : DescriptorData("LocationHook.MethodListener")
    private object MethodListenerWgs84 : DescriptorData("LocationHook.MethodListenerWgs84")
    private object MethodDefaultManager : DescriptorData("LocationHook.MethodDefaultManager")
    private object ValLatitude : DefaultData("LocationHook.ValLatitude", floatDefVal = LATITUDE_DEF_VAL)
    private object ValLongitude : DefaultData("LocationHook.ValLongitude", floatDefVal = LONGITUDE_DEF_VAL)

    private const val LATITUDE_DEF_VAL = 31.135633f
    private const val LONGITUDE_DEF_VAL = 121.66625f
    private lateinit var binding: ModuleDialogLocationBinding
    private val RedirectUIClass by "com.tencent.mm.plugin.location.ui.RedirectUI".toLazyAppClass()

    override val location = "辅助"
    override val funcName = "虚拟定位"
    override val funcDesc = "将腾讯定位SDK结果虚拟为指定经纬度"
    override var onClick: ((View) -> Unit)? = { layoutView ->
        binding = ModuleDialogLocationBinding.inflate(LayoutInflater.from(layoutView.context))
        binding.moduleDialogBtnLocationSelect.setOnClickListener {
            val activity = layoutView.context as Activity
            activity.startActivityForResult(Intent(layoutView.context, RedirectUIClass).apply { putExtra("map_view_type", 8) }, 6)
        }
        binding.moduleDialogEdtLocationLatitude.setText("${ValLatitude.floatVal}")
        binding.moduleDialogEdtLocationLongitude.setText("${ValLongitude.floatVal}")
        layoutView.context.showDialog {
            title = funcName
            view = binding.root
            positiveButton {
                ValLatitude.floatVal = binding.moduleDialogEdtLocationLatitude.text.toString().toFloat()
                ValLongitude.floatVal = binding.moduleDialogEdtLocationLongitude.text.toString().toFloat()
            }
            neutralButton("重置") {
                ValLatitude.floatVal = LATITUDE_DEF_VAL
                ValLongitude.floatVal = LONGITUDE_DEF_VAL
            }
            negativeButton()
        }
    }
    override val targetProcess = arrayOf(
        WxProcess.MAIN_PROCESS.processName,
        WxProcess.APP_BRAND_0.processName
    )
    override val isNeedRestartApp = true

    @Suppress("DEPRECATION")
    override fun initOnce() {
        RedirectUIClass.method {
            name = "onActivityResult"
            param(IntType, IntType, IntentClass)
        }.hook {
            after {
                val requestCode = args(0).cast<Int>()!!
                val resultCode = args(1).cast<Int>()!!
                if (requestCode == 6 && resultCode == Activity.RESULT_OK) {
                    val intent = args(2).cast<Intent>()!!
                    val locationIntent = intent.getParcelableExtra<Parcelable>("KLocationIntent")!!
                    val locationDataStr = locationIntent::class.java.method { returnType(StringClass) }.get(locationIntent).string()
                    val pattern = Regex("lat ([-+]?[0-9]*\\.?[0-9]+);lng ([-+]?[0-9]*\\.?[0-9]+);")
                    val match = pattern.find(locationDataStr)
                    if (match != null && match.groupValues.size == 3) {
                        binding.moduleDialogEdtLocationLatitude.setText("${match.groupValues[1].toFloatOrNull() ?: LATITUDE_DEF_VAL}")
                        binding.moduleDialogEdtLocationLongitude.setText("${match.groupValues[2].toFloatOrNull() ?: LONGITUDE_DEF_VAL}")
                    } else {
                        binding.moduleDialogEdtLocationLatitude.setText("$LATITUDE_DEF_VAL")
                        binding.moduleDialogEdtLocationLongitude.setText("$LONGITUDE_DEF_VAL")
                    }
                }
            }
        }
        listOf(
            MethodListener.desc.toDexMethod(),
            MethodListenerWgs84.desc.toDexMethod(),
            MethodDefaultManager.desc.toDexMethod()
        ).forEach { method ->
            method.hook {
                beforeIfEnabled {
                    val location = args(0).any()!!
                    location::class.java.apply {
                        method { name = "getLatitude" }.hook {
                            beforeIfEnabled {
                                result = ValLatitude.floatVal.toDouble()
                            }
                        }
                        method { name = "getLongitude" }.hook {
                            beforeIfEnabled {
                                result = ValLongitude.floatVal.toDouble()
                            }
                        }
                    }
                    removeSelf()
                }
            }
        }
    }

    override fun dexFind(dexKit: DexKitBridge) {
        MethodListener.desc = dexKit.findMethod {
            matcher {
                name = "onLocationChanged"
                usingEqStrings("MicroMsg.SLocationListener")
            }
        }.single().descriptor
        MethodListenerWgs84.desc = dexKit.findMethod {
            matcher {
                name = "onLocationChanged"
                usingEqStrings("MicroMsg.SLocationListenerWgs84")
            }
        }.single().descriptor
        MethodDefaultManager.desc = dexKit.findMethod {
            matcher {
                name = "onLocationChanged"
                usingEqStrings("MicroMsg.DefaultTencentLocationManager", "[mlocationListener]error:%d, reason:%s")
            }
        }.single().descriptor
    }
}
