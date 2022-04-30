package com.uin.constant;

/**
 * 库存状态的枚举常量
 *
 * @author wanglufei
 * @date 2022/4/30 2:21 PM
 */
public class WareStatusConstant {

    public enum PurchaseStatusEnum {
        CREATE_PURCHASE(0, "新建"),
        ASSIGNED(1, "已分配"),
        RECEIVE(2, "已领取"),
        FINISH(3, "已完成"),
        HASERROR(4, "有异常");

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        PurchaseStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

    }

    public enum PurchaseDetailsStatusEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISH(3, "已完成"),
        HASERROR(4, "采购失败");


        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        PurchaseDetailsStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

    }
}
