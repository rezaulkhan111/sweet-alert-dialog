package cn.pedant.sweet_alert

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.util.AttributeSet
import android.util.Xml
import android.view.animation.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

object OptAnimationLoader {
    @Throws(NotFoundException::class)
    fun loadAnimation(context: Context, id: Int): Animation? {
        try {
            context.resources.getAnimation(id)
                .use { parser -> return createAnimationFromXml(context, parser) }
        } catch (ex: XmlPullParserException) {
            val rnf = NotFoundException(
                "Can't load animation resource ID #0x" +
                        Integer.toHexString(id)
            )
            rnf.initCause(ex)
            throw rnf
        } catch (ex: IOException) {
            val rnf = NotFoundException(
                "Can't load animation resource ID #0x" +
                        Integer.toHexString(id)
            )
            rnf.initCause(ex)
            throw rnf
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun createAnimationFromXml(
        c: Context,
        parser: XmlPullParser,
        parent: AnimationSet? = null,
        attrs: AttributeSet = Xml.asAttributeSet(parser)
    ): Animation? {
        var anim: Animation? = null

        // Make sure we are on a start tag.
        var type: Int
        val depth = parser.depth
        while ((parser.next().also { type = it } != XmlPullParser.END_TAG || parser.depth > depth)
            && type != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG) {
                continue
            }
            when (val name = parser.name) {
                "set" -> {
                    anim = AnimationSet(c, attrs)
                    createAnimationFromXml(c, parser, anim as AnimationSet?, attrs)
                }
                "alpha" -> anim = AlphaAnimation(c, attrs)
                "scale" -> anim = ScaleAnimation(c, attrs)
                "rotate" -> anim = RotateAnimation(c, attrs)
                "translate" -> anim = TranslateAnimation(c, attrs)
                else -> anim = try {
                    Class.forName(name)
                        .getConstructor(Context::class.java, AttributeSet::class.java)
                        .newInstance(c, attrs) as Animation
                } catch (te: Exception) {
                    break
                }
            }
            parent?.addAnimation(anim)
        }
        return anim
    }
}