package lads.dev.entity;

public class ViewEntity {

    private String value;
    private int columnIndex;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public ViewEntity(String value, int columnIndex) {
        this.value = value;
        this.columnIndex = columnIndex;
    }
}
