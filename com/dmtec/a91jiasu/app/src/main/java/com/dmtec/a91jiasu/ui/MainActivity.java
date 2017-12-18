package com.dmtec.a91jiasu.ui;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.VpnService;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.os.Message;
import android.os.Process;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dmtec.a91jiasu.views.activities.PayActivity;
import com.dmtec.a91jiasu.views.activities.ProblemsActivity;
import com.dmtec.a91jiasu.views.activities.SysMsgActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import com.dmtec.a91jiasu.R;
import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.common.CustomSharePreference;
import com.dmtec.a91jiasu.common.CustomToast;
import com.dmtec.a91jiasu.common.ResourceParser;
import com.dmtec.a91jiasu.common.Timer;
import com.dmtec.a91jiasu.common.Validator;
import com.dmtec.a91jiasu.data.VpnProfile;
import com.dmtec.a91jiasu.data.VpnProfileDataSource;
import com.dmtec.a91jiasu.data.VpnType;
import com.dmtec.a91jiasu.data.VpnType.VpnTypeFeature;
import com.dmtec.a91jiasu.logic.Callbacks;
import com.dmtec.a91jiasu.logic.CharonVpnService;
import com.dmtec.a91jiasu.logic.LogicHelper;
import com.dmtec.a91jiasu.logic.VpnStateService;
import com.dmtec.a91jiasu.logic.VpnStateService.State;
import com.dmtec.a91jiasu.logic.VpnWorker;
import com.dmtec.a91jiasu.models.IPServer;
import com.dmtec.a91jiasu.models.Plan;
import com.dmtec.a91jiasu.views.activities.ConfirmMobileActivity;
import com.dmtec.a91jiasu.views.activities.LoginActivity;
import com.dmtec.a91jiasu.views.activities.WebActivity;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;


import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import pl.droidsonroids.gif.GifImageView;

import static io.rong.imkit.utils.SystemUtils.getCurProcessName;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
	private View startArea, buyArea, contactArea, userArea;
	private TextView startText, buyText, contactText,userText, conBuyBtn;
	private TextView contactPhoneNum,contactQq;
	private ImageView startIcon, buyIcon, contactIcon, userIcon,callAction,qqAction;
	private View startPage, buyPage, contactPage, userPage;
	private View sysMsgLayout,resetPwdLayout,problemsLayout,viewWebsiteLayout,shareLayout,exitLayout;
	private AlertDialog.Builder normalDialog,buyDialog;
	private GifImageView gifImageView;
	private LogicHelper helper;
	private Button btnBuy;
	private MsgReceiver msgReceiver;
	private VpnWorker vpnWorker;
	private TextView userAccount,userEXDate,startExpireDate,startShare,shareText;
	private StrongSwanApplication app;
    private SwipeRefreshLayout swipeRefreshLayout;
	public static final String START_PROFILE = "com.dmtec.a91jiasu.action.START_PROFILE";
	public static final String DISCONNECT = "com.dmtec.a91jiasu.action.DISCONNECT";
	public static final String EXTRA_VPN_PROFILE_ID = "com.dmtec.a91jiasu.VPN_PROFILE_ID";
    private ProgressDialog progressDialog;
    private View changeRouteLayout;
    private TextView routeName,switchRoute;
    private int CON_STATE = Constants.ConnectStatus.DISABLED;
    private int last = 0;
    public static int SERVER_AREA = 0;
	private String USER_TAG = "";
    private boolean autoCon = false;

	/**
	 * Use "bring your own device" (BYOD) features
	 */
	public static final boolean USE_BYOD = true;
	private static final int PREPARE_VPN_SERVICE = 0;
	private static final String PROFILE_NAME = "com.dmtec.a91jiasu.MainActivity.PROFILE_NAME";
	private static final String PROFILE_REQUIRES_PASSWORD = "com.dmtec.a91jiasu.MainActivity.REQUIRES_PASSWORD";
	private static final String DIALOG_TAG = "Dialog";

	private Bundle mProfileInfo;
	private VpnStateService mService;

	private final ServiceConnection mServiceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mService = ((VpnStateService.LocalBinder)service).getService();

			if (START_PROFILE.equals(getIntent().getAction()))
			{
				startVpnProfile(getIntent());
			}
			else if (DISCONNECT.equals(getIntent().getAction()))
			{
				disconnect();
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//strongswan method
		this.bindService(new Intent(this, VpnStateService.class), mServiceConnection, Service.BIND_AUTO_CREATE);
        USER_TAG = ((StrongSwanApplication)getApplication()).getToken();
        SERVER_AREA = CustomSharePreference.getInt(getApplicationContext(),USER_TAG + "LAST_ROUTE");
        if(SERVER_AREA == -1){
            SERVER_AREA = 0;
        }

		helper = new LogicHelper(this);

		initTabPage();

		initView();

		vpnWorker = new VpnWorker(getApplicationContext(),MainActivity.this,(GifImageView)findViewById(R.id.gif_view));

		loadData();

        loadRyToken();

		vpnWorker.prepare();

		registReciver();

        int firstLaunch = CustomSharePreference.getInt(getApplicationContext(),Constants.Flags.SP_FIRST_LAUNCH);
        if(firstLaunch == -1){
            try{
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
                if(isOpen){
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            CustomSharePreference.putInteger(getApplicationContext(),Constants.Flags.SP_FIRST_LAUNCH,0);
            showSetBatteryDialog();
        }

	}

	//注册接收器
	private void registReciver(){
		msgReceiver = new MsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.dmtec.a91jiasu.msg.receiver");
		registerReceiver(msgReceiver,intentFilter);
	}

	//后台设置提示
    private void showSetBatteryDialog(){
        String msg = getResources().getString(R.string.first_launch_hint);
        showMsgDialog(msg,"知道了",null);
    }

	/**
	 * 初始化导航栏
	 */
	private void initTabPage(){
		startArea = findViewById(R.id.start_check);
		startText = (TextView)findViewById(R.id.start_text);
		startIcon = (ImageView)findViewById(R.id.start_icon);
		startPage = findViewById(R.id.start_view);

		buyArea = findViewById(R.id.buy_check);
		buyText = (TextView)findViewById(R.id.buy_text);
		buyIcon = (ImageView)findViewById(R.id.buy_icon);
		buyPage = findViewById(R.id.buy_view);

		contactArea = findViewById(R.id.contact_check);
		contactText = (TextView)findViewById(R.id.contact_text);
		contactIcon = (ImageView)findViewById(R.id.contact_icon);
		contactPage = findViewById(R.id.contact_view);

		userArea = findViewById(R.id.user_check);
		userText = (TextView)findViewById(R.id.user_text);
		userIcon = (ImageView)findViewById(R.id.user_icon);
		userPage = findViewById(R.id.user_view);

		startArea.setOnClickListener(this);
		buyArea.setOnClickListener(this);
		contactArea.setOnClickListener(this);
		userArea.setOnClickListener(this);

		startIcon.setBackground(getResources().getDrawable(R.drawable.start1));
		startText.setTextColor(getResources().getColor(R.color.primary_btn_pressed));
		startPage.setVisibility(View.VISIBLE);
	}

	//切换线路后重置APP
	private void resetApp(){
        ((StrongSwanApplication)getApplication()).vpnPut(Constants.Flags.SP_USER_TYPE,-1);

        loadData();

        vpnWorker.prepare();
	}

	/**
	 * 实例化视图
	 */
	private void initView(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
		conBuyBtn = (TextView)findViewById(R.id.continue_buy_btn);
		conBuyBtn.setOnClickListener(this);

        sysMsgLayout = findViewById(R.id.sys_msg);
        sysMsgLayout.setOnClickListener(this);
		resetPwdLayout = findViewById(R.id.reset_pwd_layout);
		resetPwdLayout.setOnClickListener(this);
        problemsLayout = findViewById(R.id.questions);
        problemsLayout.setOnClickListener(this);
		viewWebsiteLayout = findViewById(R.id.view_website_layout);
		viewWebsiteLayout.setOnClickListener(this);
		shareLayout = findViewById(R.id.share_layout);
		shareLayout.setOnClickListener(this);
		exitLayout = findViewById(R.id.exit_layout);
		exitLayout.setOnClickListener(this);

		initDialog();

		contactPhoneNum = (TextView)findViewById(R.id.tv_phone_num);
		contactQq = (TextView)findViewById(R.id.tv_contact_qq);

		callAction = (ImageView)findViewById(R.id.call_action);
		callAction.setOnClickListener(this);

		qqAction = (ImageView)findViewById(R.id.qq_action);
		qqAction.setOnClickListener(this);

		userAccount = (TextView)findViewById(R.id.user_account);
		userEXDate = (TextView)findViewById(R.id.user_exdate);
        userEXDate.setText(CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_EXPIRE_TIME));
		startExpireDate = (TextView)findViewById(R.id.expire_date);
        startExpireDate.setText(CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_EXPIRE_TIME));

		btnBuy = (Button)findViewById(R.id.btn_buy);
		btnBuy.setOnClickListener(this);
		GradientDrawable gd = (GradientDrawable)btnBuy.getBackground();
		gd.setColor(getResources().getColor(R.color.white));
		btnBuy.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					btnBuy.getBackground().setAlpha(255);
				}else if(event.getAction() == MotionEvent.ACTION_DOWN){
					btnBuy.getBackground().setAlpha(220);
				}
				return false;
			}
		});

        //是否开启分享
        if(Constants.Config.OPEN_SHARE){
            findViewById(R.id.start_share_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.share_layout).setVisibility(View.VISIBLE);
        }

		shareText = (TextView)findViewById(R.id.share_text);
        String shareString = CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_SHARE_TEXT);
        if(!shareString.equals("")){
            shareText.setText(shareString);
        }
        startShare = (TextView)findViewById(R.id.start_share);
        startShare.setOnClickListener(this);

		initBack();
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.GREEN,Color.YELLOW);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPlanList(swipeRefreshLayout);
            }
        });

        changeRouteLayout = findViewById(R.id.change_route_layout);
        changeRouteLayout.setVisibility(View.GONE);

        routeName = (TextView)findViewById(R.id.route_name);
        routeName.setText(SERVER_AREA==0?"国内线路":"国外线路");

        switchRoute = (TextView)findViewById(R.id.switch_route);
        switchRoute.setOnClickListener(this);

        ((TextView)findViewById(R.id.buy_hint)).setSelected(true);
		findViewById(R.id.IM_BTN).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String SERVICE_ID = "KEFU151053934865188";
				String SERVICE_NAME = "91加速客服";
                Context context = getApplicationContext();
                Conversation.ConversationType conversationType = Conversation.ConversationType.CUSTOMER_SERVICE;
                Uri uri = Uri.parse("rong://" + context.getApplicationInfo().processName).buildUpon().appendPath("conversation").appendPath(conversationType.getName().toLowerCase()).appendQueryParameter("targetId", SERVICE_ID).appendQueryParameter("title", SERVICE_NAME).build();
                Intent intent = new Intent("android.intent.action.VIEW", uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
			}
		});
	}

	/**
	 * 设置主页背景
	 */
	private void initBack(){
		WindowManager wm = this.getWindowManager();
		int screenWidth = wm.getDefaultDisplay().getWidth();
		Bitmap bg = ResourceParser.drawable2bitmap(getResources().getDrawable(R.drawable.main_bg));
		int w = bg.getWidth();
		int h = bg.getHeight();
		int y = screenWidth * h / w;

		Bitmap bm = Bitmap.createScaledBitmap(bg, screenWidth, y, true);

		LinearLayout linearLayout =(LinearLayout) findViewById(R.id.start_layout);
		ViewGroup.LayoutParams lp = linearLayout.getLayoutParams();
		lp.width = screenWidth;
		lp.height = y;
		linearLayout.setLayoutParams(lp);
		linearLayout.setBackground(new BitmapDrawable(bm));

		RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.relative_layout);
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View bv = inflater.inflate(R.layout.start_btn,relativeLayout,false);
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(screenWidth/2,screenWidth/2);
		lp2.leftMargin = screenWidth/4;
		lp2.topMargin = y-screenWidth/4 + screenWidth/20;
		relativeLayout.addView(bv,lp2);

		gifImageView = (GifImageView)findViewById(R.id.gif_view);
		gifImageView.setOnClickListener(this);
	}

	/**
	 * 底部导航栏状态清空
	 */
	private void clearTab(){
		startIcon.setBackground(getResources().getDrawable(R.drawable.start0));
		startText.setTextColor(getResources().getColor(R.color.hint));
		startPage.setVisibility(View.INVISIBLE);
		buyIcon.setBackground(getResources().getDrawable(R.drawable.buy0));
		buyText.setTextColor(getResources().getColor(R.color.hint));
		buyPage.setVisibility(View.INVISIBLE);
		contactIcon.setBackground(getResources().getDrawable(R.drawable.contact0));
		contactText.setTextColor(getResources().getColor(R.color.hint));
		contactPage.setVisibility(View.INVISIBLE);
		userIcon.setBackground(getResources().getDrawable(R.drawable.user0));
		userText.setTextColor(getResources().getColor(R.color.hint));
		userPage.setVisibility(View.INVISIBLE);
	}


	/**
	 * 退出登录对话框
	 */
	private void initDialog(){
		normalDialog = new AlertDialog.Builder(MainActivity.this);
		normalDialog.setMessage("确定要退出当前账号吗?");
		normalDialog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("请稍后");
                        progressDialog.show();
                        //logout
                        disconnect();
                        helper.logout(((StrongSwanApplication) getApplication()).getToken(), new Callbacks.RequestCallback() {
                            @Override
                            public void onResult(boolean success, int status) {
                                ((StrongSwanApplication) getApplication()).setToken(null);
                                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                intent.putExtra(Constants.Flags.INTENT_LOGOUT_FROM_MAIN,true);
                                CustomSharePreference.putInteger(getApplicationContext(),Constants.Flags.SP_HAS_LOGOUT,0);
                                //断开连接
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                    }
                                });
                                RongIM.getInstance().disconnect();
                                startActivity(intent);
                                finish();
                            }
                        });
					}
				});
		normalDialog.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//...To-do
					}
				});

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("请稍候");
	}

	private void showBuyDialog(String msg){
		buyDialog = new AlertDialog.Builder(MainActivity.this);
		buyDialog.setMessage(msg);
		buyDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				clearTab();
				buyIcon.setBackground(getResources().getDrawable(R.drawable.buy1));
				buyText.setTextColor(getResources().getColor(R.color.primary_btn_pressed));
				buyPage.setVisibility(View.VISIBLE);
			}
		});
		buyDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		AlertDialog dialog = buyDialog.create();
		dialog.show();
	}

    private void loadRyToken(){
        helper.getRYToken(((StrongSwanApplication)getApplication()).getToken(), new Callbacks.RYTokenCallback() {
            @Override
            public void onRYToken(String rytyken) {
                Log.e("rytoken",rytyken+"");
                if(rytyken.equals("")){
                    runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            (findViewById(R.id.IM_BTN_VIEW)).setVisibility(View.GONE);
                        }
                    }));
                }
                connectRY(rytyken);
            }
        });
    }

	/**
	 * 拉取数据
	 */
	private void loadData(){
		app = (StrongSwanApplication)getApplication();
		loadUserInfo();
		loadPlanList(swipeRefreshLayout);
		loadService();
	}

    //当前进程
    public String getCurProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService("activity");
        List runningAppProcessInfos = mActivityManager.getRunningAppProcesses();
        if(runningAppProcessInfos == null) {
            return null;
        } else {
            Iterator var4 = runningAppProcessInfos.iterator();

            ActivityManager.RunningAppProcessInfo appProcess;
            do {
                if(!var4.hasNext()) {
                    return null;
                }

                appProcess = (ActivityManager.RunningAppProcessInfo)var4.next();
            } while(appProcess.pid != pid);

            return appProcess.processName;
        }
    }

	//连接聊天服务器
    private void connectRY(String token){
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {

                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("LoginActivity", "--onSuccess " + userid);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

	/**
	 * 加载用户信息
	 */
	private void loadUserInfo(){
		helper.getUserInfo(((StrongSwanApplication) getApplication()).getToken(), new Callbacks.GetUserInfoCallback() {
			@Override
			public void onUserInfo(final JSONObject data) {
                final String nickname = data.optString("nickname");
                final String mobile = data.optString("mobile");
                final String exdate = data.optString("exdate");
                final String share = data.optString("share");
                final int type = data.optInt("message");
                final String hasvpn = data.optString("hasvpn");
				((StrongSwanApplication)getApplication()).vpnPut(Constants.Flags.SP_USER_TYPE,type);
				JPushInterface.setAlias(getApplicationContext(),123456,nickname);
                CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_EXPIRE_TIME,exdate);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
                        if(hasvpn.equals("1")){
                            CustomSharePreference.putInteger(getApplicationContext(),"hasvpn",0);
                            changeRouteLayout.setVisibility(View.VISIBLE);
                        }else{
                            CustomSharePreference.putInteger(getApplicationContext(),"hasvpn",1);
                            changeRouteLayout.setVisibility(View.GONE);
                        }
						userAccount.setText(getResources().getString(R.string.account)+ nickname);
						userEXDate.setText(getResources().getString(R.string.exdate) + exdate);
                        if(share!=null && (!share.equals(""))){
                            shareText.setText(share);
                            CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_SHARE_TEXT,share);
                        }
                        if(type == 2){
                            startExpireDate.setTextColor(Color.RED);
                        }else{
							startExpireDate.setTextColor(getResources().getColor(R.color.hint));
						}
						startExpireDate.setText(exdate);
                        //设置过期提醒
                        setAlarm(exdate);
                        Log.e("设置过期时间:",exdate);
					}
				});
			}

			@Override
			public void onFailure(String msg) {

			}
		});
	}

    java.util.Timer alarmTimer = new java.util.Timer(true);

	//设置过期提醒
	private void setAlarm(String exdate){
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        Date dateNow = new Date(),d=null;
        String now = String.valueOf(dateNow.getTime()).substring(0,10),ex=null;
        try{
            d = format.parse(exdate);
            ex = String.valueOf(d.getTime()).substring(0,10);
        }catch (Exception e){
            e.printStackTrace();
        }
        Long dur;
        try{
            Log.e("过期时间戳",ex+"");
            Log.e("当前时间戳",now+"");
            dur = Long.parseLong(ex) - Long.parseLong(now);
            Log.e("时间差",dur+"");
        }catch (Exception e){
            return;
        }
        alarmTimer.cancel();
        if(dur > 0){
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            State state = mService.getState();
                            if((state == State.CONNECTED) || (state == State.CONNECTING)){
                                disconnect();
                            }
                            ((StrongSwanApplication)getApplication()).vpnPut(Constants.Flags.SP_USER_TYPE,2);
                            startExpireDate.setTextColor(Color.RED);
                            String msg2 = "当前套餐已到期，请及时续费";
                            showBuyDialog(msg2);
                        }
                    });
                }
            };
            alarmTimer = new java.util.Timer(true);
            alarmTimer.schedule(task,dur*1000);
        }
    }

	/**
	 * 检测网络是否连接
	 * @return
	 */
	private boolean checkNetworkState() {
		boolean flag = false;
		//得到网络连接信息
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		//去进行判断网络是否连接
		if (manager.getActiveNetworkInfo() != null) {
			flag = manager.getActiveNetworkInfo().isAvailable();
		}
		if (!flag) {
			showMsgDialog("网络不可用，请检查网络设置","确定",null);
//            gifImageView.setClickable(true);
		} else {
			work();
		}

		return flag;
	}


	private void showMsgDialog(String msg,String posBtnText,DialogInterface.OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setPositiveButton(posBtnText,listener);
		Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	/**
	 * 加载客服信息
	 */
	private void loadService(){
		String q = CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_QQ);
		String p = CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_PHONE_CN);
		if((q != null) && (p != null)){
			contactQq.setText(q);
			contactPhoneNum.setText(p);
		}
		helper.getService(new Callbacks.ServiceCallback() {
			@Override
			public void onSuccess(final String qqNum, final String phoneNum,final String phonecn) {
				CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_QQ,qqNum);
				CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_PHONE,phoneNum);
				CustomSharePreference.putString(getApplicationContext(),Constants.Flags.SP_PHONE_CN,phonecn);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						contactQq.setText(qqNum);
						contactPhoneNum.setText(phonecn);
					}
				});
			}

			@Override
			public void onFailure(final String msg) {}
		});
	}

	/**
	 * 加载购买套餐
	 */
	private void loadPlanList(final SwipeRefreshLayout swipeRefreshLayout){
        if(swipeRefreshLayout.isRefreshing()){
            loadUserInfo();
        }
		helper.getPlanList(((StrongSwanApplication) getApplication()).getToken(), new Callbacks.GetPlanCallback() {
			@Override
			public void onPlanList(final ArrayList<Plan> list) {
				Log.e("list",list.toString());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						loadPlanList(list,swipeRefreshLayout);
					}
				});
			}

			@Override
			public void onFailure(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        CustomToast.toast(MainActivity.this,getApplicationContext(),msg);
                    }
                });
			}
		});
	}

	/**
	 * 生成套餐列表
	 * @param list list
	 */
	private void loadPlanList(ArrayList<Plan> list,SwipeRefreshLayout swipeRefreshLayout){
		final int[] colors = {getResources().getColor(R.color.list_color_0), getResources().getColor(R.color.list_color_1), getResources().getColor(R.color.list_color_2)};
        ArrayList<View> items = new ArrayList<>();
		final ViewGroup parent =(ViewGroup)findViewById(R.id.container);
		parent.removeAllViews();
		for(int i = 0; i < list.size(); i++){
			final int index = i;
			final Plan plan = list.get(i);
			LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
			final View item = inflater.inflate(R.layout.plan,parent,false);
            items.add(item);
			TextView name = (TextView) item.findViewById(R.id.name);
			TextView desc = (TextView)item.findViewById(R.id.desc);
			TextView cost = (TextView)item.findViewById(R.id.cost);
			name.setText(plan.getName());
			desc.setText(plan.getDesc());
			cost.setText(plan.getCost() + "元");

			item.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					for(int j = 0; j < parent.getChildCount(); j++){
						ViewGroup view = (ViewGroup) parent.getChildAt(j);
						view.setBackgroundColor(getResources().getColor(R.color.white));
						setViewTextColor(view,getResources().getColor(R.color.black));
					}
					item.setBackgroundColor(colors[index%colors.length]);
					GradientDrawable gd = (GradientDrawable)btnBuy.getBackground();
					gd.setColor(colors[index%colors.length]);
					btnBuy.setTextColor(getResources().getColor(R.color.white));
					ViewGroup vg = (ViewGroup)v;
					setViewTextColor(vg,getResources().getColor(R.color.white));
					btnBuy.setTag(plan);
				}
			});

			parent.addView(item);
		}
		items.get(0).callOnClick();
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
	}

	/**
	 * 设置一个viewGroup下字体颜色
	 * @param vg
	 */
	private void setViewTextColor(ViewGroup vg,int color){
		for(int i = 0; i < vg.getChildCount(); i++){
			View v = vg.getChildAt(i);
			if(v instanceof TextView){
				((TextView) v).setTextColor(color);
			}else if(v instanceof ViewGroup){
				setViewTextColor((ViewGroup) v,color);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.start_check:
				clearTab();
				startIcon.setBackground(getResources().getDrawable(R.drawable.start1));
				startText.setTextColor(getResources().getColor(R.color.primary_btn_pressed));
				startPage.setVisibility(View.VISIBLE);
                if(CON_STATE == Constants.ConnectStatus.CONNECTING){
                    gifImageView.setImageResource(R.drawable.running);
                }
				break;
			case R.id.continue_buy_btn:
			case R.id.buy_check:
				clearTab();
				buyIcon.setBackground(getResources().getDrawable(R.drawable.buy1));
				buyText.setTextColor(getResources().getColor(R.color.primary_btn_pressed));
				buyPage.setVisibility(View.VISIBLE);
				break;
			case R.id.contact_check:
				clearTab();
				contactIcon.setBackground(getResources().getDrawable(R.drawable.contact1));
				contactText.setTextColor(getResources().getColor(R.color.primary_btn_pressed));
				contactPage.setVisibility(View.VISIBLE);
				break;
			case R.id.user_check:
				clearTab();
				userIcon.setBackground(getResources().getDrawable(R.drawable.user1));
				userText.setTextColor(getResources().getColor(R.color.primary_btn_pressed));
				userPage.setVisibility(View.VISIBLE);
				break;
			case R.id.reset_pwd_layout:
				Intent intent2 = new Intent(MainActivity.this, ConfirmMobileActivity.class);
				intent2.putExtra(Constants.Flags.INTENT_CHANGE_PASSWORD,true);
				startActivity(intent2);
				break;
			case R.id.view_website_layout:
				String url = Constants.getWebPageUrl();
				Intent intent = new Intent(MainActivity.this,WebActivity.class);
				intent.putExtra(Constants.Flags.INTENT_IS_BUYING,false);
				intent.putExtra(Constants.Flags.INTENT_URL,url);
				startActivity(intent);
				break;
            case R.id.sys_msg:
                Intent msgIntent = new Intent(MainActivity.this, SysMsgActivity.class);
                startActivity(msgIntent);
                break;
            case R.id.questions:
                Intent problemIntent = new Intent(MainActivity.this, ProblemsActivity.class);
                startActivity(problemIntent);
                break;
			case R.id.share_layout:
				share();
				break;
			case R.id.exit_layout:
				normalDialog.show();
				break;
			case R.id.gif_view:
//			    gifImageView.setClickable(false);
				int state = app.vpnGetInt(Constants.Flags.SP_CONNECT_STATE);
				if((state == 2)||(state == 1)){
					disconnect();
				}else if((state == 3) || (state == -1000) || (state == -1)){
					checkNetworkState();
				}
				break;
			case R.id.call_action:
			    call();
				break;
			case R.id.qq_action:
				helper.chat(contactQq.getText().toString());
				break;
            case R.id.start_share:
                share();
                break;
            case R.id.switch_route:
                Timer mTimer = new Timer(1, new Timer.TimerCallback() {
                    @Override
                    public void onTimeOut() {
                        SERVER_AREA = 1 - SERVER_AREA;
                        routeName.setText(SERVER_AREA==0?"国内线路":"国外线路");
                        CustomSharePreference.putInteger(getApplicationContext(),USER_TAG+"LAST_ROUTE",SERVER_AREA);
                        resetApp();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onTimeFailed() {}

                    @Override
                    public void onSecondPassed(int timeLeft) {}
                });
                disconnect();
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("正在切换线路");
                progressDialog.show();
                mTimer.start();
                break;
			case R.id.btn_buy:
				Plan plan = (Plan) btnBuy.getTag();
				if(plan == null){
					CustomToast.toast(MainActivity.this,getApplicationContext(),"请选择一个套餐");
				}else if(Constants.Config.USE_WEB_PAY){
                    if(CON_STATE == Constants.ConnectStatus.CONNECTING || CON_STATE == Constants.ConnectStatus.CONNECTED){
                        ((StrongSwanApplication)getApplication()).vpnPut("BUY_WITH_CONNECT",SERVER_AREA);
                        autoCon = true;
                        disconnect();
                    }
					String id = plan.getId();
					String token = ((StrongSwanApplication)getApplication()).getToken();
					String u = Constants.getPlanConfirmUrl(MainActivity.SERVER_AREA) + "token=" + token + "&id=" + id;
					Intent i = new Intent(MainActivity.this,WebActivity.class);
					i.putExtra(Constants.Flags.INTENT_IS_BUYING,true);
					i.putExtra(Constants.Flags.INTENT_URL,u);
					startActivityForResult(i,52902);
				}else if(!Constants.Config.USE_WEB_PAY){
                    Intent i = new Intent(MainActivity.this,PayActivity.class);
                    i.putExtra("MAIN.ACTIVITY.LIST.PLAN",plan);
                    startActivityForResult(i,50000);
				}
				break;
		}
	}

    private void call(){
        final String p =  CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_PHONE);
        final String pcn =  CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_PHONE_CN);
        final String ps[] ={
                "境外用户：" + p,
                "境内用户：" + pcn
        };
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("请选择要拨打的客服电话");

        builder.setItems(ps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                helper.call(which==0?p:pcn);
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


	final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				//已经ping出了最优服务器,准备连接
				case 0:
				    if(SERVER_AREA == 0){
                        connect(vpnWorker.bestIKEV20Server,this);
                    }else if(SERVER_AREA == 1){
                        connect(vpnWorker.bestIKEV21Server,this);
                    }
					break;
				//连接中
				case 1:
				    if(CON_STATE != Constants.ConnectStatus.CONNECTING){
                        CON_STATE = Constants.ConnectStatus.CONNECTING;
                    }
					break;
				//已连接
				case 2:
//				    gifImageView.setClickable(true);
                    if(CON_STATE != Constants.ConnectStatus.CONNECTED){
                        CON_STATE = Constants.ConnectStatus.CONNECTED;
                        if(!autoCon){
                            CustomToast.toast(MainActivity.this,getApplicationContext(),"加速成功");
                        }else{
                            autoCon = false;
                            CustomToast.toast(MainActivity.this,getApplicationContext(),"加速成功");
                        }
                        gifImageView.setImageResource(R.drawable.finish);
                    }
                    if(SERVER_AREA == 0){
                        Constants.useAbroadService = false;
                    }else{
                        Constants.useAbroadService = true;
                    }
                    break;
				//断开连接
				case 3:
//                    gifImageView.setClickable(true);
                    Constants.useAbroadService = Constants.isAroad;
                    if(CON_STATE != Constants.ConnectStatus.DISCONNECTED){
                        CON_STATE = Constants.ConnectStatus.DISCONNECTED;
                        if(!autoCon){
                            CustomToast.toast(MainActivity.this,getApplicationContext(),"已停止加速");
                        }
                        gifImageView.setImageResource(R.drawable.ready);
                        if(((StrongSwanApplication)getApplication()).getToken()!=null){
                            loadUserInfo();
                        }
                    }
					break;
				//连接失败
				case -1000:
//                    gifImageView.setClickable(true);
				    if(CON_STATE != Constants.ConnectStatus.FAILED){
                        CON_STATE = Constants.ConnectStatus.FAILED;
                        CustomToast.toast(MainActivity.this,getApplicationContext(),"加速失败");
                        gifImageView.setImageResource(R.drawable.ready);

                        if(((StrongSwanApplication)getApplication()).getToken()!=null){
                            loadUserInfo();
                        }
                    }
					break;
				//用户未付费
				case -2:
//				    gifImageView.setClickable(true);
					gifImageView.setImageResource(R.drawable.ready);
					String m1 = "您的有效期不足，请先购买套餐";
					showBuyDialog(m1);
					break;
				//用户账户到期
				case -3:
//                    gifImageView.setClickable(true);
					gifImageView.setImageResource(R.drawable.ready);
					String msg2 = "您的有效期不足，请先购买套餐";
					showBuyDialog(msg2);
					break;
                //新的点击事件
                case -4:
                    break;
			}
		}
	};


	private void share(){
        final int share = CustomSharePreference.getInt(getApplicationContext(),Constants.Flags.SP_FIRST_SHARE);
        if(share == -1){
            String msg = getString(R.string.first_share_hint);
            showMsgDialog(msg,"继续分享", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CustomSharePreference.putInteger(getApplicationContext(),Constants.Flags.SP_FIRST_SHARE,0);
                    toShare();
                }
            });
        }else{
            toShare();
        }
	}

	private void toShare(){
		UMImage img = new UMImage(MainActivity.this,R.drawable.ic_launcher);
		img.setThumb(img);
		final UMImage logo = img;
		final String title = getResources().getString(R.string.app_name);
		final String webTitle = getResources().getString(R.string.web_title);
		final String desc = getResources().getString(R.string.description);
		final String url = Constants.getShareUrl() + CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_USERNAME);
        final boolean wx = Validator.isWeixinAvilible(getApplicationContext());
        final boolean qq = Validator.isQQClientAvailable(getApplicationContext());
		ShareBoardlistener shareBoardlistener = new  ShareBoardlistener() {

			@Override
			public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
				if (share_media==null){
					//根据key来区分自定义按钮的类型，并进行对应的操作
					if (snsPlatform.mKeyword.equals("share_qq")){
                        if(!qq){
                            CustomToast.asynsToast(getApplicationContext(),MainActivity.this,"请先安装QQ");
                        }else{
                            UMWeb web = new UMWeb(url);
                            web.setThumb(logo);
                            web.setTitle(title);
                            web.setDescription(desc);
                            new ShareAction(MainActivity.this).withMedia(web).setPlatform(SHARE_MEDIA.QQ).share();
                        }
					}
					if (snsPlatform.mKeyword.equals("share_qzone")){
                        if(!qq){
                            CustomToast.asynsToast(getApplicationContext(),MainActivity.this,"请先安装QQ");
                        }else{
                            UMWeb web = new UMWeb(url);
                            web.setThumb(logo);
                            web.setTitle(title);
                            web.setDescription(desc);
                            new ShareAction(MainActivity.this).withMedia(web).setPlatform(SHARE_MEDIA.QZONE).share();
                        }
					}
					if (snsPlatform.mKeyword.equals("share_timeline")){
                        if(!wx){
                            CustomToast.asynsToast(getApplicationContext(),MainActivity.this,"请先安装微信");
                        }else{
                            UMWeb web = new UMWeb(url);
                            web.setThumb(logo);
                            web.setTitle(webTitle);
                            new ShareAction(MainActivity.this).withMedia(web).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).share();
                        }
					}
					if (snsPlatform.mKeyword.equals("share_wechat")){
                        if(!wx){
                            CustomToast.asynsToast(getApplicationContext(),MainActivity.this,"请先安装微信");
                        }else{
                            UMWeb web = new UMWeb(url);
                            web.setThumb(logo);
                            web.setTitle(title);
                            web.setDescription(desc);
                            new ShareAction(MainActivity.this).withMedia(web).setPlatform(SHARE_MEDIA.WEIXIN).share();
                        }
					}
				}
			}
		};

		new ShareAction(MainActivity.this)
				.addButton("share_qq","share_qq","share_qq","share_qq")
				.addButton("share_qzone","share_qzone","share_qzone","share_qzone")
				.addButton("share_timeline","share_timeline","share_timeline","share_timeline")
				.addButton("share_wechat","share_wechat","share_wechat","share_wechat")
				.setShareboardclickCallback(shareBoardlistener)
				.open();
	}

	private int getUT(){
		Object ut = ((StrongSwanApplication)getApplication()).vpnGet(Constants.Flags.SP_USER_TYPE);
		return (Integer)((ut==null) ? -1:ut);
	}

	/**
	 * Vpn 连接
	 */
	private void work(){
		gifImageView.setImageResource(R.drawable.running);
		new Thread(new Runnable() {
			@Override
			public void run() {

				//服务器信息获取完毕 && 用户付费信息获取完毕
                if(MainActivity.SERVER_AREA == 0){
                    while ((getUT() == -1) || (vpnWorker.bestIKEV20Server == null)){}
                }else{
                    while ((vpnWorker.bestIKEV21Server == null) || (getUT() == -1)){}
                }
				switch(getUT()){
					//付费未到期
					case 1:
						handler.sendEmptyMessage(0);
						break;
					//未付费
					case 0:
						handler.sendEmptyMessage(-2);
						break;
					//付费已到期
					case 2:
						handler.sendEmptyMessage(-3);
						break;
				}
			}
		}).start();
	}



	/**
	 * 链接VPN
	 * @param server vpn服务器
	 * @param handler handler
	 */
	private void connect(final IPServer server,final Handler handler){

        String alias = CustomSharePreference.getString(getApplicationContext(),Constants.Config.CERTIFICATE_ALIAS);
        Log.e("alias",alias+"");
        if(alias.equals("")){
            alias = null;
        }

        //2.Vpn连接配置
        VpnProfile p = new VpnProfile();
        p.setName(server.getHost());
        p.setGateway(server.getHost());
        p.setVpnType(VpnType.IKEV2_EAP);
        p.setUsername(CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_USERNAME));
        p.setPassword(CustomSharePreference.getString(getApplicationContext(),Constants.Flags.SP_PASSWORD));
        p.setCertificateAlias(alias);
        p.setSelectedAppsHandling(VpnProfile.SelectedAppsHandling.SELECTED_APPS_DISABLE);
//        p.setSelectedAppsHandling(VpnProfile.SelectedAppsHandling.SELECTED_APPS_EXCLUDE);
//        p.setSelectedApps("com.dmtec.a91jiasu");
        p.setFlags(0);
        VpnProfileDataSource dataSource = new VpnProfileDataSource(getApplicationContext());
        dataSource.open();
        ArrayList<VpnProfile> list = (ArrayList<VpnProfile>) dataSource.getAllVpnProfiles();
        for(int i = 0; i < list.size(); i++){
            dataSource.deleteVpnProfile(list.get(i));
        }
        p = dataSource.insertProfile(p);
        if(p != null){
            startVpnProfile(p,handler);
        }
        dataSource.close();
	}

	/**
	 * Start the given VPN profile
	 *
	 * @param profile VPN profile
	 */
	public void startVpnProfile(VpnProfile profile,Handler handler)
	{
		Bundle profileInfo = new Bundle();
		profileInfo.putLong(VpnProfileDataSource.KEY_ID, profile.getId());

		profileInfo.putString(VpnProfileDataSource.KEY_USERNAME, profile.getUsername());

		profileInfo.putString(VpnProfileDataSource.KEY_PASSWORD, profile.getPassword());

		profileInfo.putBoolean(PROFILE_REQUIRES_PASSWORD, profile.getVpnType().has(VpnTypeFeature.USER_PASS));
		profileInfo.putString(PROFILE_NAME, profile.getName());

		prepareVpnService(profileInfo,handler);
	}

	/**
	 * Prepare the VpnService. If this succeeds the current VPN profile is
	 * started.
	 *
	 * @param profileInfo a bundle containing the information about the profile to be started
	 */
	protected void prepareVpnService(Bundle profileInfo,Handler handler)
	{
		Intent intent;
		try
		{
			intent = VpnService.prepare(this);
		}
		catch (IllegalStateException ex)
		{
			/* this happens if the always-on VPN feature (Android 4.2+) is activated */
			VpnNotSupportedError.showWithMessage(this, R.string.vpn_not_supported_during_lockdown);
			return;
		}
		/* store profile info until the user grants us permission */
		mProfileInfo = profileInfo;
		if (intent != null)
		{

			try
			{
				startActivityForResult(intent, PREPARE_VPN_SERVICE);
			}
			catch (ActivityNotFoundException ex)
			{

				VpnNotSupportedError.showWithMessage(this, R.string.vpn_not_supported);
			}
		}
		else
		{	/* user already granted permission to use VpnService */
			onActivityResult(PREPARE_VPN_SERVICE, RESULT_OK, null);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case PREPARE_VPN_SERVICE:
				if (resultCode == RESULT_OK && mProfileInfo != null)
				{
					Intent intent = new Intent(this, CharonVpnService.class);
					intent.putExtras(mProfileInfo);
					this.startService(intent);
				}
				break;
			case 52902:
				if(resultCode == 20925){
                    int constate = app.vpnGetInt("BUY_WITH_CONNECT");
                    if(constate != -1){
                        app.vpnPut("BUY_WITH_CONNECT",-1);
                        SERVER_AREA = constate;
//                        gifImageView.callOnClick();
                    }
					loadUserInfo();
					if(data.getBooleanExtra(Constants.Flags.AR_PAY_SUCCESS,false)){
						clearTab();
						startIcon.setBackground(getResources().getDrawable(R.drawable.start1));
						startText.setTextColor(getResources().getColor(R.color.primary_btn_pressed));
						startPage.setVisibility(View.VISIBLE);
					}
				}
				break;
            case 50000:
                if(resultCode == 50001){
                    loadUserInfo();
                    if(data.getBooleanExtra(Constants.Flags.AR_PAY_SUCCESS,false)){
                        clearTab();
                        startIcon.setBackground(getResources().getDrawable(R.drawable.start1));
                        startText.setTextColor(getResources().getColor(R.color.primary_btn_pressed));
                        startPage.setVisibility(View.VISIBLE);
                    }
                }
                break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}



	/**
	 * 页面重载检测状态
	 */
	private void reloadState(){
		int state = app.vpnGetInt(Constants.Flags.SP_CONNECT_STATE);
		switch (state){
			//已连接
			case 2:
				gifImageView.setImageResource(R.drawable.finish);
				break;
			default:
				gifImageView.setImageResource(R.drawable.ready);
				break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		reloadState();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(msgReceiver);
		if (mService != null)
		{
			this.unbindService(mServiceConnection);
		}
	}


	/**
	 * Due to launchMode=singleTop this is called if the Activity already exists
	 */
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		if (START_PROFILE.equals(intent.getAction()))
		{
			startVpnProfile(intent);
		}
		else if (DISCONNECT.equals(intent.getAction()))
		{
			disconnect();
		}
	}

	/**
	 * Start the VPN profile referred to by the given intent. Displays an error
	 * if the profile doesn't exist.
	 *
	 * @param intent Intent that caused us to start this
	 */
	private void startVpnProfile(Intent intent)
	{
		long profileId = intent.getLongExtra(EXTRA_VPN_PROFILE_ID, 0);
		if (profileId <= 0)
		{	/* invalid invocation */
			return;
		}
		VpnProfileDataSource dataSource = new VpnProfileDataSource(this);
		dataSource.open();
		VpnProfile profile = dataSource.getVpnProfile(profileId);
		dataSource.close();

		if (profile != null)
		{
			startVpnProfile(profile,handler);
		}
		else
		{
			Toast.makeText(this, R.string.profile_not_found, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Disconnect the current connection, if any (silently ignored if there is no connection).
	 */
	private void disconnect()
	{
		if (mService != null && (mService.getState() == State.CONNECTED || mService.getState() == State.CONNECTING))
		{
			mService.disconnect();
		}
	}

	/**
	 * Class representing an error message which is displayed if VpnService is
	 * not supported on the current device.
	 */
	public static class VpnNotSupportedError extends AppCompatDialogFragment
	{
		static final String ERROR_MESSAGE_ID = "com.dmtec.a91jiasu.VpnNotSupportedError.MessageId";

		public static void showWithMessage(AppCompatActivity activity, int messageId)
		{
			Bundle bundle = new Bundle();
			bundle.putInt(ERROR_MESSAGE_ID, messageId);
			VpnNotSupportedError dialog = new VpnNotSupportedError();
			dialog.setArguments(bundle);
			dialog.show(activity.getSupportFragmentManager(), DIALOG_TAG);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			final Bundle arguments = getArguments();
			final int messageId = arguments.getInt(ERROR_MESSAGE_ID);
			return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.vpn_not_supported_title)
				.setMessage(messageId)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.dismiss();
					}
				}).create();
		}
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //此处写退向后台的处理
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

	/**
	 * 广播接收器
	 * @author len
	 *
	 */
	public class MsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
            int state = intent.getIntExtra("state", -1);
            handler.sendEmptyMessage(state);
		}
	}
}
