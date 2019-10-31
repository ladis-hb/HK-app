package lads.dev.entity;

public class DevEntity {
    private int id;
    private String typeCode;
    private String protocolCode;
    private String name;
    private String code;
    private String spNo;
    private String spCode;
    private String onlineFlag;
    private int seq;
    private int lostTimes; //丢失连接次数

    public DevEntity() {}

    public DevEntity(String typeCode, String protocolCode, String name,String code, String spNo,String spCode,String onlineFlag,int seq) {
        this.typeCode=typeCode;
        this.protocolCode = protocolCode;
        this.name = name;
        this.code = code;
        this.spNo = spNo;
        this.spCode = spCode;
        this.onlineFlag = onlineFlag;
        this.seq = seq;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getProtocolCode() {
        return protocolCode;
    }

    public void setProtocolCode(String protocolCode) {
        this.protocolCode = protocolCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSpNo() {
        return spNo;
    }

    public void setSpNo(String spNo) {
        this.spNo = spNo;
    }

    public String getSpCode() {
        return spCode;
    }

    public void setSpCode(String spCode) {
        this.spCode = spCode;
    }

    public String getOnlineFlag() {
        return onlineFlag;
    }

    public void setOnlineFlag(String onlineFlag) {
        this.onlineFlag = onlineFlag;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getLostTimes() {
        return lostTimes;
    }

    public void setLostTimes(int lostTimes) {
        this.lostTimes = lostTimes;
    }
}
