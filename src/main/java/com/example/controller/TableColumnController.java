package com.example.controller;

import com.example.enums.OrderStatusEnum;
import com.example.utils.EnumUtil;
import com.example.vo.ResultVo;
import com.example.vo.TableColumnVo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * 枚举字段信息
 */
@Slf4j
@RestController
@RequestMapping("/tablecolumn")
public class TableColumnController {

    /**
     *
     * 订单管理 - 订单列表 - 查询条件 - 订单状态接口
     */
    @PostMapping("/getorderstatusenumlist")
    public ResultVo getOrderStatusEnumList(){
        Class<OrderStatusEnum> clasz= OrderStatusEnum.class;
        Map<Object, String> map= EnumUtil.EnumToMap(clasz);
        return ResultVo.success(getEnumList(map));
    }


    /**
     * 获取枚举List
     * @param map
     * @return
     */
    private List<TableColumnVo> getEnumList(Map<Object, String> map){
        List<TableColumnVo> tableColumnList = new ArrayList();
        for (Map.Entry<Object, String> entry : map.entrySet()) {
            TableColumnVo tableColumnVo =  new TableColumnVo();
            Object key = entry.getKey();
            if (null !=key){
                tableColumnVo.setColumnValue(entry.getKey().toString());
                tableColumnVo.setValueDesc(entry.getValue());
                tableColumnList.add(tableColumnVo);
            }
        }
        return tableColumnList;
    }




}
