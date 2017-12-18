package com.dmtec.a91jiasu.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dmtec.a91jiasu.models.IPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class PingUtil
{
    public static String getAvgRTT(String ipStr, int count, int timeout)
    {
        String cmd = createSimplePingCommand(count, timeout, ipStr);
        String pingString = ping(cmd);
        if (null != pingString)
        {
            try
            {
                String tempInfo = pingString.substring(pingString.indexOf("min/avg/max/mdev") + 19);
                String[] temps = tempInfo.split("/");
                return temps[1];
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return "-1";
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    private static String ping(String command)
    {
        Process process = null;
        try
        {
            process = Runtime.getRuntime().exec(command);
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while (null != (line = reader.readLine()))
            {
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
            is.close();
            return sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != process)
            {
                process.destroy();
            }
        }
        return null;
    }

    private static String createSimplePingCommand(int count, int timeout, String ip)
    {
        return "/system/bin/ping -c " + count + " -w " + timeout + " " + ip;
    }

    public static void chooseIP(final ArrayList<IPServer> list, final IPChooseCallback callback){
        final int size = list.size();
        if(size == 0){callback.onResult(null);}
        final Handler handler = new Handler(){
            int count = 0;
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    count++;
                    if(count == size){
                        callback.onResult(getBestIPServer(list));
                    }
                }
            }
        };
        for(int i = 0; i < size; i++){
            final int index = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ip = list.get(index).getIP();
                    String rtt = getAvgRTT(ip, 3 , 2);
                    list.get(index).setRtt(rtt);
                    handler.sendEmptyMessage(0);
                }
            }).start();
        }
    }

    private static IPServer getBestIPServer(ArrayList<IPServer> list){
        if(list.size() <= 1){
            return (list.size() == 0) ? null : list.get(0);
        }
        IPServer server = list.get(0);
        for (int i = 1 ;i<list.size();i++){
            Log.e(server.getHost() + " : ",server.getRtt()+" ms");
            if((server.getRtt() > list.get(i).getRtt()) && (list.get(i).getRtt()>0)){
                server = list.get(i);
            }
        }
        return server;
    }

    public static String GetInetAddress(String  host){
        String IPAddress = "";
        InetAddress ReturnStr1 = null;
        try {
            ReturnStr1 = InetAddress.getByName(host);
            IPAddress = ReturnStr1.getHostAddress();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return  IPAddress;
        }
        return IPAddress;
    }

    public interface IPChooseCallback{
        void onResult(IPServer ipServer);
    }
}
