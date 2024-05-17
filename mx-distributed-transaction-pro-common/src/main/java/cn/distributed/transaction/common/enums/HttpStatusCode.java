package cn.distributed.transaction.common.enums;

/**
 * @author 昴星
 * @date 2023-10-24 23:25
 * @explain
 */
public enum HttpStatusCode {

    OK("200", "处理正常"),

    File_PART_LOAD_ERROR("201", "文件块上传异常请重新上传"),

    FILE_PART_FILE_LOAD_MISSING_CONDITIONS("202", "文件上传参数缺失"),
    FILE_PART_MISSING("205", "文件块数据不一致，请重新上传"),

    ILLEAGE_PARAM("2000001","参数无效");

    public String code;

    public String msg;

    HttpStatusCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    ;

}
