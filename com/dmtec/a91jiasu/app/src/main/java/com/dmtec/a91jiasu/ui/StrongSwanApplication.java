/*
 * Copyright (C) 2014 Tobias Brunner
 * Hochschule fuer Technik Rapperswil
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.  See <http://www.fsf.org/copyleft/gpl.txt>.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 */

package com.dmtec.a91jiasu.ui;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import com.dmtec.a91jiasu.common.Constants;
import com.dmtec.a91jiasu.security.LocalCertificateKeyStoreProvider;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;

public class StrongSwanApplication extends Application
{
	private static Context mContext;

	private Map<String, Object> varsMap = new HashMap<>();

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

    public String getANDROID_ID() {
        return ANDROID_ID;
    }

    private String token = "";

    private String ANDROID_ID;

	static {
		Security.addProvider(new LocalCertificateKeyStoreProvider());
	}

	public void  vpnPut(String name, Object obj){
		varsMap.put(name, obj);
	}

	public Object vpnGet(String key){
		if(varsMap.containsKey(key)) {
			return varsMap.get(key);
		}
		return null;
	}

	public int vpnGetInt(String key){
		Object o = vpnGet(key);
		if(o == null){
			return -1;
		}else{
			return (int)o;
		}
	}

	public void vpnRemove(String key){
		varsMap.remove(key);
	}

	public void vpnClearMap(){
		varsMap.clear();
	}



	@Override
	public void onCreate()
	{
		super.onCreate();
		StrongSwanApplication.mContext = getApplicationContext();
//        Config.DEBUG = true;
        //设置分享id、secret
		PlatformConfig.setWeixin(Constants.ShareID.WECHAT_ID,Constants.ShareID.WECHAT_SECRET);
		PlatformConfig.setQQZone(Constants.ShareID.QQ_ID,Constants.ShareID.QQ_SECRET);
		UMShareAPI.get(this);

        JPushInterface.init(getApplicationContext());

		ANDROID_ID =android.provider.Settings.Secure.getString(getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
        Log.e("ANDROID_ID",ANDROID_ID);

        RongIM.init(this);

	}

	/**
	 * Returns the current application context
	 * @return context
	 */
	public static Context getContext()
	{
		return StrongSwanApplication.mContext;
	}
}
