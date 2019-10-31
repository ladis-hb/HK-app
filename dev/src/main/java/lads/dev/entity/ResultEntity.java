package lads.dev.entity;

public class ResultEntity {
    private int id;
    private String instructionCode;
    private String fieldName;
    private String displayName;
    private String prefix;
    private String suffix;
    private int startAddr;
    private int len;
    private int ratio;
    private String dataType;
    private String dataType2;
    private String parseFlag;
    private String parseStr;
    private String warnType;
    private String lowerLimit;
    private String upperLimit;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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

    public int getRatio() {
        return ratio;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getParseFlag() {
        return parseFlag;
    }

    public void setParseFlag(String parseFlag) {
        this.parseFlag = parseFlag;
    }

    public String getParseStr() {
        return parseStr;
    }

    public void setParseStr(String parseStr) {
        this.parseStr = parseStr;
    }

    public String getWarnType() {
        return warnType;
    }

    public void setWarnType(String warnType) {
        this.warnType = warnType;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDataType2() {
        return dataType2;
    }

    public void setDataType2(String dataType2) {
        this.dataType2 = dataType2;
    }
}
