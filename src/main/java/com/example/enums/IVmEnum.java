package com.example.enums;

import com.example.vo.OptionVo;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tengdj
 * @date 2019/9/4 10:50
 **/
public interface IVmEnum<T> {

    String getDesc();

    T getValue();

    default String getType(){
        return "";
    }

    default List<OptionVo> getOptionVos(IVmEnum[] e) {
        return Arrays.stream(e).filter(i -> i.getDesc() != null && !"".equals(i.getDesc())).map(i -> new OptionVo(i.getDesc(), String.valueOf(i.getValue()))).collect(Collectors.toList());
    }

    default List<OptionVo> getOptionVos(IVmEnum[] e, String type) {
        return Arrays.stream(e).filter(i -> i.getType().equals(type)).map(i -> new OptionVo(i.getDesc(), String.valueOf(i.getValue()))).collect(Collectors.toList());
    }

    default IVmEnum getItem(IVmEnum[] arr, String desc) {
        for (IVmEnum item : arr) {
            if (item.getDesc().equals(desc)) {
                return item;
            }
        }
        return this;
    }

}
