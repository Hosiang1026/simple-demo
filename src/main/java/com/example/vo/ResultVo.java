package com.example.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Howe Hsiang
 */
@Data
public class ResultVo<T> implements Serializable {

    private static final long serialVersionUID = -2982594497132614928L;
    /**
     * 返回结果编码
     */
    private String Code;

    /**
     * 返回结果说明
     */
    private String Message;


    /**
     *  返回内容【Json字符串】
     */
    private String Content;

    private T data;

    public ResultVo(String code, String message, String content) {
        Code = code;
        Message = message;
        Content = content;
    }

    public ResultVo(String code, String message, T data) {
        Code = code;
        Message = message;
        this.data = data;
    }

    public static <T> ResultVo<T> success(T t) {
        return new ResultVo("200", "成功",  t);
    }

    public static <T> ResultVo<T> success(String content) {
        return new ResultVo("200", "成功", content);
    }

    public static <T> ResultVo<T> fail(String content) {
        return new ResultVo("400", "失败", content);
    }

}
