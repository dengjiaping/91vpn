package com.dmtec.a91jiasu.logic;

import com.dmtec.a91jiasu.models.IPServer;
import com.dmtec.a91jiasu.models.Plan;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by dmtec on 2017/8/20.
 * 各种回调
 */

public class Callbacks {

    /**
     * 一般的无实质数据返回的网络请求回调
     */
    public interface RequestCallback{
        void onResult(boolean success, int status);
    }

    public interface AreaCallback{
        void onResult(boolean isAbroad);
    }

    /**
     * 验证码
     */
    public interface RequestSmsCodeCallback{
        void onResult(boolean success,int status,String msg);
    }

    /**
     * 修改、找回密码时验证手机号及验证码回调
     */
    public interface CheckAppVerifyCallback{
        void onSuccess(String uid, String username);
        void onFailure(String msg);
    }

    /**
     * 登录回调
     */
    public interface LoginCallback{
        void onSuccess(String token);
        void onFailure(int status, String msg);
    }

    /**
     * 注册
     */
    public interface RegisterCallback{
        void onResult(boolean success ,int status, String msg);
    }

    /**
     * 客服信息回调
     */
    public interface ServiceCallback{
        void onSuccess(String qq, String phone,String phonecn);
        void onFailure(String msg);
    }

    public interface RYTokenCallback{
        void onRYToken(String rytyken);
    }

    /**
     *  套餐列表回调
     */
    public interface GetPlanCallback{
        void onPlanList(ArrayList<Plan> list);
        void onFailure(String msg);
    }

    /**
     * 服务器列表回调
     */
    public interface GetServerCallback{
        void onServerList(ArrayList<IPServer> ikev20,ArrayList<IPServer> ikev21,ArrayList<IPServer> ss0,ArrayList<IPServer> ss1);
        void onFailure(String msg);
    }

    /**
     * userinfo回调
     */
    public interface GetUserInfoCallback{
        void onUserInfo(JSONObject data);
        void onFailure(String msg);
    }

    /**
     * 支付状态查询
     */
    public interface GetPayStateCallback{
        void onStatus(String tradeStatus);
    }

    /**
     * 证书查询
     */
    public interface QueryCertCallback{
        void onResult(boolean success,boolean isLatest,int version,String curl);
    }

    public interface StreamCallback{
        void onResult(boolean success,InputStream in);
    }

    public interface CheckAccountCallback{
        void onResult(boolean success,String msg);
    }
}
