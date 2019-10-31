package lads.dev.entity;

public class LastWarnValueEntity {
    private String instructionCode;
    private String fieldName;
    private int seq;
    private String value;

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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
