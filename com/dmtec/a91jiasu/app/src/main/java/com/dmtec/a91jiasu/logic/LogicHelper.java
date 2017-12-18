package com.dmtec.a91jiasu.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomHttpClient;
import com.dmtec.a91jiasu.common.CustomSharePreference;
import com.dmtec.a91jiasu.common.CustomToast;
import com.dmtec.a91jiasu.common.Validator;
import com.dmtec.a91jiasu.models.IPServer;
import com.dmtec.a91jiasu.models.Plan;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dmtec.a91jiasu.ui.MainActivity;
import com.dmtec.a91jiasu.ui.StrongSwanApplication;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by dmtec on 2017/8/18.
 * APP 逻辑封装
 */

public class LogicHelper {
    private Context context;
    private CustomHttpClient httpClient;

    public LogicHelper(Context context){
        this.context = context;
        httpClient = CustomHttpClient.getInstance();
     }

    //编辑框获取焦点
     public void requestFocus(EditText editText){
         editText.setFocusable(true);
         editText.setFocusableInTouchMode(true);
         editText.requestFocus();
     }

    //调起拨号盘
    public void call(String number){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    //与指定QQ聊天
    public void chat(String qq){
        String url="mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }else{
            Toast.makeText(context,"请安装手机QQ",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 验证手机号是否注册
     * @param account 手机号
     * @param type =username 写死
     * @param callback 回调
     */
    public void checkAccount(String account, String type, final Callbacks.CheckAccountCallback callback){
        //url
        String url = Constants.getCheckAccountUrl();
        Log.e("url",url);

        //params
        HashMap<String, String> map = new HashMap<>();
        map.put("account",account);
        map.put("type",type);

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response,JSONObject result) {
                Log.e("response:",result.toString());
                String status = result.optString("status","2000");
                String msg = result.optString("msg","请求错误");
                if(status.equals("0000")){
                    callback.onResult(true,msg);
                }else if(status.equals("1000")){
                        callback.onResult(false, msg);
                }else{
                    callback.onResult(false,msg);
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onResult(false,"网络请求失败");
            }
        });
    }

    /**
     * 请求不同类型的短信验证码
     * @param account 用户名
     * @param kind 注册=regist 修改、找回密码=change
     * @param callback 回调
     */
    public void requestSmsCode(String account, String kind, final Callbacks.RequestSmsCodeCallback callback){
        String action = "member";
        String type = "mobile";
        if(Validator.isEmail(account)){
            type = "email";
        }
        String url = "";
        if(kind.equals("regist")){
            url = Constants.getSendAppVerifyUrl();
        }else if(kind.equals("change")){
            url = Constants.getSendAppChangeVerifyUrl();
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("account",account);
        map.put("type",type);
        map.put("action",action);
        map.put("kind",kind);

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response,JSONObject result) {
                Log.e("result",result.toString());
                String status = result.optString("status","1000");
                if(status.equals("0000")){
                    callback.onResult(true,0,result.optString("msg",""));
                }else{
                    callback.onResult(false, 1,result.optString("msg",""));
                }
            }
            @Override
            public void onFailure(Call call, Exception e) {
                    callback.onResult(false,1,"网络错误");
            }
        });
    }

    /**
     * 修改/找回 密码时确认用户名和验证码
     * @param username 用户名=手机号
     * @param regVerify 验证码
     * @param callback 回调
     */
    public void checkAppVerify(String username, String regVerify, final Callbacks.CheckAppVerifyCallback callback){

        String url = Constants.getCheckAppVerifyUrl();
        String type = "mobile";
        if(Validator.isEmail(username)){
            type = "email";
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("reg_verify",regVerify);
        map.put("reg_type",type);

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response: ", result.toString());

                String status = result.optString("status","1000");
                String msg = result.optString("msg","");
                JSONObject data = result.optJSONObject("data");
                if(data == null){
                    data = new JSONObject();
                }
                String uid = data.optString("id","");
                String username = data.optString("username");
                if(status.equals("0000")){
                    callback.onSuccess(uid,username);
                }else{
                    callback.onFailure(msg);
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onFailure("网络不可用，请检查设置");
            }
        });
    }

    /**
     * 登录操作
     * @param username 用户名=手机号
     * @param pwd 密码
     * @param callback 回调
     */
    public void login(String username, String pwd, String deviceId, final Callbacks.LoginCallback callback){

        String url = Constants.getLoginUrl();
        Log.e("login",url);
        HashMap<String, String> map= new HashMap<>();
        map.put("username",username);
        map.put("password",pwd);
        map.put("device_id",deviceId);

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response: ", result.toString());
                String status = result.optString("status","2000");
                String msg = result.optString("msg");
                String token = result.optString("token");
                String has = result.optString("has");
                Log.e("has: ", has);
                if(status.equals("0000") && has.equals("0")){
                    callback.onSuccess(token);
                }else if(status.equals("0000")&&has.equals("1")){
                    //其它设备已登录,将新token存放在msg中
                    Log.e("onFaile: ", token);
                    callback.onFailure(2,token);
                }else{
                    callback.onFailure(1,msg);
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onFailure(-1,"网络不可用，请检查设置");
            }
        });
    }

    /**
     * 注册账号
     * @param username 用户名=手机号
     * @param password 密码
     * @param regVerify 验证码
     */
    public void register(String username, String password, String regVerify, final Callbacks.RegisterCallback callback){
        String url = Constants.getRegisterUrl();

        String type = "mobile";
        if(Validator.isEmail(username)){
            type = "email";
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("username",username);
        map.put("password",password);
        map.put("repassword",password);
        map.put("verify","");
        map.put("qq","");
        map.put("email","");
        map.put("reg_type",type);
        map.put("role","1");
        map.put("reg_verify",regVerify);

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                String status = result.optString("status");
                String msg = result.optString("msg");
                if(status.equals("0000")){
                    callback.onResult(true,0,msg);
                }else{
                    callback.onResult(false,1,msg);
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onResult(false,2,"网络错误");
            }
        });
    }


    /**
     * 找回、修改密码
     * @param username 用户名
     * @param password 新密码
     * @param callback 回调
     */
    public void setPassword(String username, String password, final Callbacks.RequestCallback callback){

        String url = Constants.getChangePasswordAppUrl();

        HashMap<String, String> map = new HashMap<>();
        map.put("new_password",password);
        map.put("username",username);
        map.put("new_re_password",password);

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response:",result.toString());
                String status = result.optString("status");
                if(status.equals("0000")){
                    callback.onResult(true,0);
                }else{
                    callback.onResult(false,1);
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onResult(false,-1);
            }
        });
    }


    /**
     * 获取客服信息
     * @param callback 回调
     */
    public void getService(final Callbacks.ServiceCallback callback){
        String url = Constants.getServiceUrl();

        HashMap<String,String> map = new HashMap<>();

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response:",result.toString());
                String status = result.optString("status");
                if(status.equals("0000")){
                    JSONArray infos = result.optJSONArray("data");
                    if(infos == null){infos = new JSONArray();}
                    if(infos.length()>0){
                        String phone = infos.optJSONObject(0).optString("phone");
                        String phonecn = infos.optJSONObject(0).optString("phonecn");
                        String qq = infos.optJSONObject(0).optString("qq");
                        callback.onSuccess(qq,phone,phonecn);
                    }else{
                        callback.onSuccess("2301545830","0086214008008088","4008008088");
                    }
                }else{
                    callback.onFailure("信息获取失败");
                }

            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onFailure("信息获取失败");
            }
        });
    }

    public void isAbroad(final Callbacks.AreaCallback callback){
        String url = "http://ip.taobao.com/service/getIpInfo2.php";

        HashMap<String, String> map = new HashMap<>();
        map.put("ip","myip");

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response:",result.toString());
                JSONObject data = result.optJSONObject("data");
                String country_id = "CN";
                if(data!=null){
                    country_id = data.optString("country_id", "CN");
                }
                callback.onResult(!country_id.equals("CN"));
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onResult(false);
            }
        });
    }

    public void getRYToken(String token, final Callbacks.RYTokenCallback callback){
        String url = Constants.getRYTokenUrl();

        HashMap<String,String> map = new HashMap<>();
        map.put("token", token);

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response:", result.toString());
                String status = result.optString("status");
                JSONObject data = result.optJSONObject("data");
                String ry = "";
                if(data!=null){
                    ry =  data.optString("rytoken");
                }
                callback.onRYToken(ry);
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onRYToken("");
            }
        });
    }

    /**
     * 退出登录
     * @param token token
     * @param callback 回调
     */
    public void logout(String token, final Callbacks.RequestCallback callback){
        String url = Constants.getLogoutUrl();

        HashMap<String,String> map = new HashMap<>();
        map.put("token", token);

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response:", result.toString());
                String status = result.optString("status");
                if(status.equals("0000")){
                    callback.onResult(true,0);
                }else{
                    callback.onResult(false,1);
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onResult(false,-1);
            }
        });

    }

    /**
     * 登录时若其它设备已登录，则可以调用这个接口强制登录，刷新服务器token
     * @param newToken 在新设备上登录所产生的token
     * @param callback 回调
     */
    public void forceLogin(String newToken,String username,String pwd,final Callbacks.RequestCallback callback){

        String url = Constants.getForceLoginUrl();

        final HashMap<String,String> map = new HashMap<>();
        map.put("token", newToken);
        map.put("username", username);
        map.put("password", pwd);

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response:",result.toString());
                String status = result.optString("status");
                String msg = result.optString("msg");
                if(status.equals("0000")){
                    callback.onResult(true,0);
                }else{
                    callback.onResult(false,1);
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onResult(false,2);
            }
        });

    }

    /**
     *  获取用户信息
     * @param token token
     * @param callback 回调
     */
    public void getUserInfo(String token, final Callbacks.GetUserInfoCallback callback){
        String url = Constants.getUserInfoUrl(MainActivity.SERVER_AREA);
        HashMap<String,String> map = new HashMap<>();
        map.put("token", token);
        map.put("type","android");
        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response:",result.toString());
                String status = result.optString("status");
                if(status.equals("0000")){
                    JSONObject data = result.optJSONObject("data");
                    if(data == null){data = new JSONObject();}
                    callback.onUserInfo(data);
                }else{
                    callback.onFailure(result.optString("msg"));
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onFailure("获取用户信息失败");
            }
        });
    }


    /**
     * 获取套餐list
     * @param token token
     * @param callback 套餐列表回调
     */
    public void getPlanList(String token, final Callbacks.GetPlanCallback callback){

        String url = Constants.getPlanUrl(MainActivity.SERVER_AREA);

        HashMap<String,String> map = new HashMap<>();
        map.put("token", token);
//        map.put("type","debug");

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response:",result.toString());
                String status = result.optString("status");
                if(status.equals("0000")){
                    ArrayList<Plan> list = new ArrayList<Plan>();

                    JSONArray data = result.optJSONArray("data");
                    if(data == null){
                        data = new JSONArray();
                    }
                    int size = data.length();
                    if(size == 0){
                        callback.onPlanList(list);
                    }else{
                        for(int i = 1; i <= data.length(); i++){
                            JSONObject obj = data.optJSONObject(i-1);
                            list.add(new Plan(
                                    obj.optString("id"),
                                    obj.optString("name"),
                                    obj.optString("desc"),
                                    obj.optString("cost"),
                                    obj.optString("duration")
                            ));
                        }
                        callback.onPlanList(list);
                    }
                }else{
                    callback.onFailure("获取信息失败");
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onFailure("获取信息失败");
            }
        });
    }


    /**
     * 查询证书是否需要更新
     * @param localVersion 当前本地证书版本号
     * @param certCallback 回调
     */
    public void queryCert(final int localVersion, final Callbacks.QueryCertCallback certCallback){
        String url = Constants.getCertificateInfo();
        HashMap<String,String> map = new HashMap<>();

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                int version = Integer.parseInt(result.optString("version","-1"));
                String url = result.optString("url",null);
                Log.e("result",version+" "+url);
                if(version > localVersion){
                    certCallback.onResult(true,false,version,url);
                }else{
                    certCallback.onResult(true,true,version,url);
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                certCallback.onResult(false,false,-1,null);
            }
        });
    }

    /**
     * 获取输入流
     * @param url url
     * @param callback 回调
     */
    public void getInputStream(String url,final Callbacks.StreamCallback callback){
        httpClient.stream(url, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                ResponseBody body = response.body();
                if(body!=null){
                    InputStream in = body.byteStream();
                    callback.onResult(true,in);
                }else{
                    callback.onResult(false,null);
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onResult(false,null);
            }
        });
    }


    private ArrayList<IPServer> fillList(JSONArray list){
        ArrayList<IPServer> servers = new ArrayList<>();
        for(int i = 0; i < list.length(); i++){
            JSONObject o = list.optJSONObject(i);
            servers.add(new IPServer(
                    o.optString("id"),
                    o.optString("vpn"),
                    o.optString("remoteID"),
                    o.optString("localID"),
                    o.optString("host"),
                    o.optString("port"),
                    o.optString("area"),
                    o.optString("type"),
                    o.optString("status"),
                    o.optString("create_time"),
                    o.optString("update_time")
            ));
            Log.e(list.toString(),o.toString());
        }
        return servers;
    }
    /**
     * 获取服务器列表
     * @param token token
     * @param callback 回调
     */
    public void getServer(String token, final Callbacks.GetServerCallback callback){

        String url = Constants.getServerUrl(MainActivity.SERVER_AREA);

        HashMap<String, String> map = new HashMap<>();
        map.put("token",token);
        map.put("sys_type","android");

        httpClient.postFormData(url, map, new CustomHttpClient.HttpCallback() {
            @Override
            public void onSuccess(Call call, Response response, JSONObject result) {
                Log.e("response:",result.toString());
                String status = result.optString("status");
                if(status.equals("0000")){
                    JSONObject lists = result.optJSONObject("data");
                    if(lists == null){
                        lists = new JSONObject();
                        try{
                            lists.put("ikev20",new JSONArray());
                            lists.put("ikev21",new JSONArray());
                            lists.put("ss0",new JSONArray());
                            lists.put("ss1",new JSONArray());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    JSONArray i0 = lists.optJSONArray("IKEv20");
                    JSONArray i1 = lists.optJSONArray("IKEv21");
                    JSONArray s0 = lists.optJSONArray("SS0");
                    JSONArray s1 = lists.optJSONArray("SS1");
                    JSONArray jsonArray = new JSONArray();
                    ArrayList<IPServer> ikev20 = fillList(i0 == null?jsonArray:i0);
                    ArrayList<IPServer> ikev21 = fillList(i1 == null?jsonArray:i1);
                    ArrayList<IPServer> ss0 = fillList(s0 == null?jsonArray:s0);
                    ArrayList<IPServer> ss1 = fillList(s1 == null?jsonArray:s1);
                    callback.onServerList(ikev20,ikev21,ss0,ss1);
                }else{
                    callback.onFailure(result.optString("msg"));
                }
            }

            @Override
            public void onFailure(Call call, Exception e) {
                callback.onFailure("获取信息失败");
            }
        });
    }

    //支付宝支付前，从后台获取订单信息
    public void aliPrePay(String token,String orderId){

    }

}
