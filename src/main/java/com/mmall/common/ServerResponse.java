package com.mmall.common;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 高复用服务响应对象的设计思想及抽象封装
 * <pre>
 * {
 *     status: 1,
 *     msg: "用户名不存在",
 *     data: []
 * }
 * </pre>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
// 指示仅包含具有非空值的属性的值，即 json 序列化时，如果属性值为 null，那么该属性会消失，如失败时不显示data。
public class ServerResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private int status; // 响应的状态码

    private String msg; // 响应的文本信息

    private T data; // 响应的数据，为了通用，设置为泛型

    // 私有化所有构造器
    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    // 只需要提供 getter
    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    /**
     * 是否是一个成功的响应，即 status 是否为 0
     *
     * @return boolean
     */
    @JsonIgnore // 使之不在 json 序列化结果当中显示
    public boolean isSuccess() {
        return this.status == ServerResponseCode.SUCCESS.getCode();
    }

    /**
     * 情景1：响应成功时，返回状态码和描述信息（status 和 "SUCCESS"）
     *
     * @return { status: 0, msg: "SUCCESS" }
     */
    public static <T> ServerResponse<T> createBySuccess() {
        return new ServerResponse<>(ServerResponseCode.SUCCESS.getCode(), ServerResponseCode.SUCCESS.getDesc());
    }

    /**
     * 情景2：响应成功时，返回状态码和消息（status 和 msg）
     *
     * @param msg 消息
     * @return { status: 0, msg: xxx }
     */
    public static <T> ServerResponse<T> createBySuccessMsg(String msg) {
        return new ServerResponse<>(ServerResponseCode.SUCCESS.getCode(), msg);
    }

    /**
     * 情景3：响应成功时，返回状态码和数据（status 和 data）
     *
     * @param data 数据
     * @return { status: 0, data: xxx }
     */
    public static <T> ServerResponse<T> createBySuccessData(T data) {
        return new ServerResponse<>(ServerResponseCode.SUCCESS.getCode(), data);
    }

    /**
     * 情景4：响应成功时，返回状态码、消息和数据（status、msg 和 data）
     *
     * @param msg 消息
     * @param data 数据
     * @return { status: 0, msg: xxx, data: xxx }
     */
    public static <T> ServerResponse<T> createBySuccessMsgData(String msg, T data) {
        return new ServerResponse<>(ServerResponseCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 情景5：响应失败时，返回状态码和描述信息（status 和 "ERROR"）
     *
     * @return { status: 1, msg: "ERROR" }
     */
    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<>(ServerResponseCode.ERROR.getCode(), ServerResponseCode.ERROR.getDesc());
    }

    /**
     * 情景6：响应失败时，返回状态码和消息（status 和 msg）
     *
     * @param errorMsg 消息
     * @return { status: 1, msg: xxx }
     */
    public static <T> ServerResponse<T> createByErrorMsg(String errorMsg) {
        return new ServerResponse<>(ServerResponseCode.ERROR.getCode(), errorMsg);
    }

    /**
     * 情景7：响应失败时（特殊错误，如参数非法2、需要登录10等），返回状态码和消息（status 和 msg）
     *
     * @param errorCode 可变的响应状态码
     * @param errorMsg 错误消息
     * @return { status: x, msg: xxx }
     */
    public static <T> ServerResponse<T> createByErrorCodeMsg(int errorCode, String errorMsg) {
        return new ServerResponse<>(errorCode, errorMsg);
    }

}
