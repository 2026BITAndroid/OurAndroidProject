package com.YanandWang.ourandroidproject.ui

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.Security
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {
    private const val TAG = "EmailSender"

    // ====================== 必须修改这4行 ======================
    private const val SENDER_EMAIL = "androidyanandwang@163.com"
    private const val SENDER_PASSWORD = "KTUSZSgMXGJX8uQH"
    private const val RECEIVER_EMAIL_YAN = "3182219477@qq.com"
    private const val RECEIVER_EMAIL_WANG = "wxw_meow@outlook.com"
    // ========================================================

    private const val APP_VERSION_NAME = "1.0"
    private const val APP_VERSION_CODE = 1

    private const val SMTP_HOST = "smtp.163.com"
    private const val SMTP_PORT = "465"

    private val globalScope = CoroutineScope(Dispatchers.IO)

    // 初始化时禁用不安全的加密算法，解决TLS握手问题
    init {
        Security.setProperty("crypto.policy", "unlimited")
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.3")
    }

    fun sendFeedbackEmailAsync(developerName: String, content: String) {
        globalScope.launch {
            var isSuccess = false
            for (attempt in 1..3) {
                Log.d(TAG, "第 $attempt 次尝试发送邮件")
                isSuccess = sendFeedbackEmail(developerName, content)
                if (isSuccess) break
                if (attempt < 3) delay(3000) // 延长重试间隔到3秒
            }
            Log.d(TAG, if (isSuccess) "✅ 邮件最终发送成功" else "❌ 3次尝试全部失败")
        }
    }

    private fun sendFeedbackEmail(developerName: String, content: String): Boolean {
        return try {
            Log.d(TAG, "163邮箱(465端口) -> 开始发送反馈")

            val receiverEmail = when (developerName) {
                "Yan" -> RECEIVER_EMAIL_YAN
                "Wang" -> RECEIVER_EMAIL_WANG
                else -> RECEIVER_EMAIL_YAN
            }

            // ✅ 终极TLS兼容配置（专门解决移动网络超时问题）
            val props = Properties()
            props["mail.smtp.host"] = SMTP_HOST
            props["mail.smtp.port"] = SMTP_PORT
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.ssl.enable"] = "true"
            props["mail.smtp.ssl.protocols"] = "TLSv1.2 TLSv1.3"
            props["mail.smtp.ssl.enabledProtocols"] = "TLSv1.2 TLSv1.3"
            // 🔥 关键：指定加密套件，解决163服务器握手不响应问题
            props["mail.smtp.ssl.ciphersuites"] = "TLS_AES_128_GCM_SHA256 TLS_AES_256_GCM_SHA384 TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256 TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"
            // 🔥 关键：禁用SSL回退，强制使用现代TLS
            props["mail.smtp.ssl.allowlegacy"] = "false"
            props["mail.smtp.starttls.required"] = "false"
            // 延长所有超时时间到45秒
            props["mail.smtp.connectiontimeout"] = "20000"
            props["mail.smtp.timeout"] = "45000"
            props["mail.smtp.writetimeout"] = "20000"
            // 开启调试（临时，成功后关闭）
            props["mail.debug"] = "true"

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD)
                }
            })

            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(SENDER_EMAIL, "时间胶囊APP", "UTF-8"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail))
                subject = "时间胶囊APP - 用户吐槽反馈"
                setText(
                    """
                    用户吐槽内容：
                    $content

                    ------------------------
                    设备与应用信息：
                    设备型号：${android.os.Build.MODEL}
                    系统版本：Android ${android.os.Build.VERSION.RELEASE}
                    应用版本：$APP_VERSION_NAME ($APP_VERSION_CODE)
                    """.trimIndent(),
                    "UTF-8"
                )
            }

            // 🔥 关键：手动连接并发送，替代静态方法
            val transport = session.getTransport("smtp")
            transport.connect(SMTP_HOST, SENDER_EMAIL, SENDER_PASSWORD)
            transport.sendMessage(message, message.allRecipients)
            transport.close()

            Log.d(TAG, "✅ 163邮件发送成功！")
            true

        } catch (e: Exception) {
            Log.e(TAG, "❌ 163邮件发送失败", e)
            false
        }
    }
}