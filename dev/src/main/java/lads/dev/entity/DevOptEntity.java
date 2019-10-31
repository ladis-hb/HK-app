package lads.dev.entity;

public class DevOptEntity {
    private int id;
    private String protocolCode;
    private String optCode;
    private String optName;
    private String optValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProtocolCode() {
        return protocolCode;
    }

    public void setProtocolCode(String protocolCode) {
        this.protocolCode = protocolCode;
    }

    public String getOptCode() {
        return optCode;
    }

    public void setOptCode(String optCode) {
        this.optCode = optCode;
    }

    public String getOptName() {
        return optName;
    }

    public void setOptName(String optName) {
        this.optName = optName;
    }

    public String getOptValue() {
        return optValue;
    }

    public void setOptValue(String optValue) {
        this.optValue = optValue;
    }
}
