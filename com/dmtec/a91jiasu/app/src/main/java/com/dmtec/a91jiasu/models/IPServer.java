package com.dmtec.a91jiasu.models;

/**
 * Created by dmtec on 2017/8/18.
 *
 */

public class IPServer {
    //ip地址
    private String IP;
    //往返延时
    private float rtt;

    //列表id
    private String id;
    //协议类型
    private String vpn;
    //远程ID
    private String remoteID;
    //本地ID
    private String localID;
    //主机
    private String host;
    //port
    private String port;
    //area
    private String area;
    //类型 0-免费 1-付费
    private String type;
    //status 1-启用 0-未启用
    private String status;
    private String createTime;
    private String updateTime;

    public IPServer(String IP) {
        this.IP = IP;
        this.rtt = 100000f;
    }

    public IPServer(String IP, float rtt) {
        this.IP = IP;
        this.rtt = rtt;
    }

    public IPServer(String id, String vpn, String remoteID, String localID, String host, String port, String area, String type, String status, String createTime, String updateTime) {
        this.id = id;
        this.vpn = vpn;
        this.remoteID = remoteID;
        this.localID = localID;
        this.host = host;
        this.port = port;
        this.area = area;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVpn() {
        return vpn;
    }

    public void setVpn(String vpn) {
        this.vpn = vpn;
    }

    public String getRemoteID() {
        return remoteID;
    }

    public void setRemoteID(String remoteID) {
        this.remoteID = remoteID;
    }

    public String getLocalID() {
        return localID;
    }

    public void setLocalID(String localID) {
        this.localID = localID;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public float getRtt() {
        return rtt;
    }

    public void setRtt(float rtt) {
        this.rtt = rtt;
    }
    public void setRtt(String rtt) {
        this.rtt = Float.parseFloat(rtt);
    }
}
