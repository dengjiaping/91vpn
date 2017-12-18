package com.dmtec.a91jiasu.logic;

import com.dmtec.a91jiasu.models.Plan;

/**
 * Created by dmtec on 2017/9/25.
 * 用于支付
 */

public class Payment {

    //用于支付的套餐订单
    private Plan plan;

    //
    private Payment(){}

    public Payment(Plan plan){
        this.plan = plan;
    }

    public boolean pay(boolean wxPay){
        return wxPay ? wxPay():aliPay();
    }

    private boolean wxPay(){
        return true;
    }


    private boolean aliPay(){
        return false;
    }

}
