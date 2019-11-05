package com.example.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 *  支付方式
 *  0待支付/1已支付/2已完成/3已关闭/4售后中/5售后完成
 * @author tengdj
 * @date 2019/8/13 20:18
 **/
public enum OrderStatusEnum implements IVmEnum, IEnum<String> {

    PREPAY("0", "待支付"),
    PAYED("1", "已支付"),
    COMPLETED("2", "已完成"),
    CLOSED("3", "已关闭"),
    SERVING("4", "售后中"),
    SERVED("5", "售后完成");


    OrderStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private String value;
    private String desc;


    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    @JsonValue
    public String getDesc() {
        return desc;
    }

    @JsonCreator
    public static OrderStatusEnum getItem(String desc){
        /*for(OrderStatusEnum item : values()){
            if(item.getDesc().equals(desc)){
                return item;
            }
        }
        return PAYED;*/
        return (OrderStatusEnum) OrderStatusEnum.PREPAY.getItem(values(), desc);
    }
}
