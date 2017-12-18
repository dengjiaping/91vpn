package com.dmtec.a91jiasu.common;

/**
 * Created by dmtec on 2017/8/16.
 * save constants
 */

public class Constants {

    //应该用哪边的服务器
    //app启动时会判断用户地理位置，国内用国内接口服务器，国外用国外接口服务器
    //加速成功后，由于网络出口变成了加速服务器，所以要根据加速服务器的位置来切换接口服务器
    public static boolean useAbroadService = true;
    //z真实地理位置
    //断开连接后，网络出口由加速服务器变回本机，若本机在国内就用国内接口服务器，在国外就用国外接口服务器
    public static boolean isAroad = true;

    private static String url1 = "http://www.bydanet.cn/index.php?s=";
    private static String url2 = "http://www.91jiasu.top/index.php?s=";
    private static String url3 = "http://www.i1y8.com/index.php?s=";


    //验证手机号是否注册
    public static String getCheckAccountUrl(){
        String path = "v15/Mem/checkAccount";
        return (useAbroadService ? url2 : url1) + path;
    }

    //获取注册验证码
    public static String getSendAppVerifyUrl(){
        String path = "ucenter/verify/sendAppVerify";
        return (useAbroadService ? url2 : url1) + path;
    }

    //找回密码时确认用户名和手机验证码
    public static String getCheckAppVerifyUrl(){
        String path = "v15/Mem/checkAppVerify";
        return (useAbroadService ? url2 : url1) + path;
    }


    //获取修改密码验证码
    public static String getSendAppChangeVerifyUrl(){
        String path = "ucenter/verify/sendAppChangeVerify";
        return (useAbroadService ? url2 : url1) + path;
    }

    //注册
    public static String getRegisterUrl(){
        String path = "v15/Mem/register";
        return (useAbroadService ? url2 : url1) + path;
    }

    //登录
    public static String getLoginUrl(){
        String path = "v15/Mem/login";
        return (useAbroadService ? url2 : url1) + path;
    }

    //强制登录
    public static String getForceLoginUrl(){
        String path = "v15/Mem/login2";
        return (useAbroadService ? url2 : url1) + path;
    }

    //找回密码
    public static String getChangePasswordAppUrl(){
        String path = "v15/Mem/changepasswordapp";
        return (useAbroadService ? url2 : url1) + path;
    }

    //客服信息
    public static String getServiceUrl(){
        String path = "v15/Mem/getService";
        return (useAbroadService ? url2 : url1) + path;
    }

    //退出登录
    public static String getLogoutUrl(){
        String path = "v15/Mem/logout";
        return (useAbroadService ? url2 : url1) + path;
    }

    //用户信息
    public static String getUserInfoUrl(int serverArea){
        String path = "v15/Mem/userInfo";
        if((serverArea == 0) && useAbroadService){
            return url2 + path;
        }else if((serverArea == 0) && (!useAbroadService)){
            return url1 + path;
        }else{
            return url3 + path;
        }
    }

    //价格列表
    public static String getPlanUrl(int serverArea){
        String path = "v15/Mem/getPlan";
        if((serverArea == 0) && useAbroadService){
            return url2 + path;
        }else if((serverArea == 0) && (!useAbroadService)){
            return url1 + path;
        }else{
            return url3 + path;
        }
    }

    //服务器列表
    public static String getServerUrl(int serverArea){
        String path = "v15/Mem/getAllServer";
        if((serverArea == 0) && useAbroadService){
            return url2 + path;
        }else if((serverArea == 0) && (!useAbroadService)){
            return url1 + path;
        }else{
            return url3 + path;
        }
    }

    //获取聊天token
    public static String getRYTokenUrl(){
        String path = "v15/Mem/getRYToken";
        return (useAbroadService ? url2 : url1) + path;
    }

    //获取最新证书
    public static String getCertificateInfo(){
        String path = "v15/Mem/getAndroidCert";
        return (useAbroadService ? url2 : url1) + path;
    }

    //获取订单页
    public static String getPlanConfirmUrl(int serverArea){
        if(serverArea == 0){
            return "http://91jiasu.adclick.com.cn/index.php?s=home/index/pay_v15.html&";
        }else{
            return "http://www.i1y8.com/index.php?s=home/index/pay_v15.html&";
        }
    }

    //获取Referer
    public static String getRefererUrl(int serverArea){
        if(serverArea == 0){
            return "http://91jiasu.adclick.com.cn";
        }else{
            return "http://www.i1y8.com";
        }
    }

    //官网
    public static String getWebPageUrl(){
        String u1 = "http://www.bydanet.cn";
        String u2 = "http://www.91jiasu.top";
        return (useAbroadService ? u2 : u1);
    }

    //注册协议
    public static String getUserProUrl(){
        String u1 = "http://www.bydanet.cn/home/index/text.html";
        String u2 = "http://www.91jiasu.top/home/index/text.html";
        return (useAbroadService ? u2 : u1);
    }

    //apk
    public static String getApkUrl(){
        String u1 = "http://www.bydanet.cn/91jiasu.apk";
        String u2 = "http://www.91jiasu.top/91jiasu.apk";
        return (useAbroadService ? u2 : u1);
    }
    //share
    public static String getShareUrl(){
        String u1 = "http://www.bydanet.cn/home/index/register.html&mobile=";
        String u2 = "http://www.91jiasu.top/home/index/register.html&mobile=";
        return (useAbroadService ? u2 : u1);
    }


    public class Config{
        //分享功能的开关
        public static final boolean OPEN_SHARE = true;

        //Toast 样式
        public static final boolean USE_DEFAULT_TOAST = true;

        //支付方式,true-网页支付 false-原生支付
        public static final boolean USE_WEB_PAY = true;

        //网络请求超时
        public static final int NETWORK_CONNECT_TIMEOUT = 10;

        //验证码等待时间
        public static final int SMS_CODE_TIMEOUT = 60;

        //默认证书
        public static final String CERTIFICATE_NAME = "ca.cert.pem";

        //证书版本
        public static final String CERTIFICATE_VERSION = "CERTIFICATE_VERSION";

        //证书别名
        public static final String CERTIFICATE_ALIAS = "CERTIFICATE_ALIAS";
    }

    public class ShareID{
        public static final String WECHAT_ID = "wx44d7fe0656422a5a";
        public static final String WECHAT_SECRET = "44769fa4d7901ed7010129eb520227f1";
        public static final String QQ_ID = "1106303331";
        public static final String QQ_SECRET = "PAOgQNr4e9lkJaPh";
    }

    public class Flags{

        //Intent Data
        public static final String INTENT_CHANGE_PASSWORD = "CHANGE_PASSWORD";
        public static final String INTENT_LOGOUT_FROM_MAIN = "LOGOUT_FROM_MAIN";
        public static final String INTENT_URL = "url";
        public static final String INTENT_USER_PRO = "user_pro";
        public static final String INTENT_IS_BUYING = "isBuying";
        public static final String INTENT_DISCONNECT_AND_LOGOUT = "DIS_AND_LOGOUT";

        //SharePreference Flag
        public static final String SP_QQ = "qq";
        public static final String SP_PHONE = "phone";
        public static final String SP_PHONE_CN = "phonecn";
        public static final String SP_REMEMBER_PASSWORD = "rememberpassword";
        public static final String SP_PASSWORD = "password";
        public static final String SP_USERNAME = "username";
        public static final String SP_HAS_LOGOUT = "hasLogout";
        public static final String SP_EXPIRE_TIME = "exp_time";
        public static final String SP_USER_TYPE = "USER_TYPE";
        public static final String SP_CONNECT_STATE = "CONNECT_STATE";
        public static final String SP_CERTIFICATE_NUM = "CERTIFICATE_NUM";
        public static final String SP_SHARE_TEXT = "SHARE_TEXT";
        public static final String SP_FIRST_LAUNCH = "FIRST_LAUNCH";
        public static final String SP_FIRST_SHARE = "FIRST_SHARE";

        //For Activity Result
        public static final String AR_PAY_SUCCESS = "pay_success";
        public static final String AR_FROM_BUY = "fromBuy";
    }

    public class ConnectStatus{
        public static final int DISABLED = 0;
        public static final int CONNECTING = 1;
        public static final int CONNECTED = 2;
        public static final int DISCONNECTING = 3;
        public static final int DISCONNECTED = 4;
        public static final int FAILED = 5;

    }
}
