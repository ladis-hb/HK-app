package lads.dev.entity;

public class WarnCfgEntity {
    private int id;
    private String instructionCode;
    private String fieldName;
    private int startAddr;
    private int len;
    private String warnEnum;
    private int seq;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInstructionCode() {
        return instructionCode;
    }

    public void setInstructionCode(String instructionCode) {
        this.instructionCode = instructionCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getWarnEnum() {
        return warnEnum;
    }

    public void setWarnEnum(String warnEnum) {
        this.warnEnum = warnEnum;
    }

    public int getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(int startAddr) {
        this.startAddr = startAddr;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
