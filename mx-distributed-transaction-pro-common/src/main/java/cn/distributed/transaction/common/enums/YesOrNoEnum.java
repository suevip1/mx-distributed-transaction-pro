package cn.distributed.transaction.common.enums;

public enum YesOrNoEnum {
    Yes(1,"是"),
    No(0,"否");

    public Integer value;
    public String des;

    YesOrNoEnum(Integer value, String des) {
        this.value = value;
        this.des = des;
    }
}
