package com.dangjia.acg.common.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Service接口服务异常基类,用于返回接口访问返回值的处理
 *
 * @author QiYuXiang
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = -7320959498334150097L;

    /**
     * 日志记录
     */
    private static Logger logger = LoggerFactory.getLogger(BaseException.class);

    private String platform;

    /***
     * 错误编码
     */
    private ServerCode serverCode;

    /***
     * 异常包含的附件
     */
    private Object attachment;

    /**
     * 异常信息描述
     */
    private String desc;

    public BaseException(ServerCode serverCode) {
        super();
        this.serverCode = null != serverCode ? serverCode : ServerCode.SERVER_EXCEPTION_NULLPOINTER;
        this.desc = this.serverCode.getDesc();
    }

    public BaseException(ServerCode serverCode, String desc) {
        super(desc);
        this.serverCode = null != serverCode ? serverCode : ServerCode.SERVER_EXCEPTION_NULLPOINTER;
        this.desc = (null == desc || desc.length() == 0) ? this.serverCode.getDesc() : desc;
    }

    public BaseException(ServerCode serverCode, String desc,String platform) {
        super(desc);
        this.serverCode = null != serverCode ? serverCode : ServerCode.SERVER_EXCEPTION_NULLPOINTER;
        this.desc = (null == desc || desc.length() == 0) ? this.serverCode.getDesc() : desc;
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public ServerCode getServiceCode() {
        return this.serverCode;
    }

    public int getCode() {
        return this.serverCode.getCode();
    }

    public String getDesc() {
        return this.desc;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public Object getAttachment() {
        return attachment;
    }

}
