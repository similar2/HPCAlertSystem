package edu.sustech.hpc.constant;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-11-14 15:38
 */
public class CaptchaConstant {

    public static final Integer Captcha_LENGTH = 6;
    public static final String REGISTER_EMAIL_CAPTCHA = "REGISTER:emailCaptcha:";
    public static final String REGISTER_EMAIL_SEND_COUNT = "REGISTER:emailSendCount:";
    public static final Integer MAX_SEND_COUNT_PER_DAY = 3;
    public static final String REGISTER_CONTENT_TEMPLATE;
    public static final String FOUND_CONTENT_TEMPLATE;
    public static final String REGISTER_EMAIL_SUBJECT = "SUSTech-HPC-Monitor注册邮箱验证";
    public static final String FOUND_EMAIL_SUBJECT = "SUSTech-HPC-Monitor找回密码邮箱验证";
    public static final Long CAPTCHA_EXPIRE_TIME = 600L; // 单位秒
    public static final Long MIN_RETRY_TIME = 60L;  // 重发限制时间，单位秒

    public static final String CAPTCHA_RETRY_TOO_SOON = "%d秒后可重新发送";
    public static final String CAPTCHA_MAX_SEND_LIMIT_EXCEEDED = "当天验证码发送次数超过限制：%d次";
    public static final String CAPTCHA_TYPE_NOT_SUPPORTED = "不支持的获取验证码类型";
    public static final String CAPTCHA_ERROR = "验证码错误";
    public static final String CAPTCHA_NOT_SEND_OR_EXPIRED = "验证码未发送或已过期";
    public static final String CAPTCHA_VERIFICATION_FAILED = "验证码校验失败，请重新获取";

    static {
        REGISTER_CONTENT_TEMPLATE = "注册验证码: %s, 有效期" + CAPTCHA_EXPIRE_TIME / 60 + "分钟";
        FOUND_CONTENT_TEMPLATE = "找回密码验证码: %s, 有效期" + CAPTCHA_EXPIRE_TIME / 60 + "分钟";
    }
}
