package com.hyp.learn.blog.business.enums;

/**
 * 用户隐私
 *
 * @author hyp
 * Project name is blog
 * Include in com.hyp.learn.core.business.enums
 * hyp create at 20-3-22
 **/
public enum UserPrivacyEnum {
    /**
     *
     */
    PUBLIC(1, "公开"),
    PRIVATE(2, "不公开"),
    ;
    private Integer code;
    private String desc;

    UserPrivacyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UserPrivacyEnum get(Integer code) {
        if (null == code) {
            return PUBLIC;
        }
        UserPrivacyEnum[] enums = UserPrivacyEnum.values();
        for (UserPrivacyEnum anEnum : enums) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return PUBLIC;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
