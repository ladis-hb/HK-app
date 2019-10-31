package lads.dev.entity;

public class KeyValueEntity {
    private String key;
    private String value;
    private String value2;

    public KeyValueEntity() {

    }

    public KeyValueEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyValueEntity(String key, String value, String value2) {
        this.key = key;
        this.value = value;
        this.value2 = value2;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }
}
