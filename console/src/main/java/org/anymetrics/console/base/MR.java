package org.anymetrics.console.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MR<T> implements Serializable {
    public static final Integer CODE_0 = 0;
    public static final Integer CODE_1 = 1;
    public static final Integer CODE_SUCCESS = 1;
    public static final Integer CODE_SYSTEM_ERROR = 0;
    public static final Integer CODE_BIZ_ERROR = 2;
    private static final long serialVersionUID = 8503450990050365408L;
    private Integer code = 1;
    private String message = "success";
    private Map<String, Object> data = new HashMap();

    public MR() {
    }

    public MR put(String attrName, Object obj) {
        this.data.put(attrName, obj);
        return this;
    }

    public MR setResultPage(ResultPage resultPage) {
        this.data.put("total", resultPage.getTotalCount());
        this.data.put("page", resultPage.getCurrentPage());
        this.data.put("size", resultPage.getPageSize());
        this.data.put("list", resultPage.getItems());
        return this;
    }

    public static MR error() {
        return error(0, "未知异常，请联系管理员");
    }

    public static MR error(String msg) {
        return error(0, msg);
    }

    public static MR error(int code, String msg) {
        MR r = new MR();
        r.setCode(code);
        r.setMessage(msg);
        return r;
    }

    public static MR ok(String msg) {
        MR r = new MR();
        r.setMessage(msg);
        return r;
    }

    public static MR ok() {
        return new MR();
    }

    public MR setList(List<T> list) {
        this.data.put("list", list);
        return this;
    }

    public MR setEntity(T t) {
        this.data.put("entity", t);
        return this;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
