package lads.dev.dto;

public class RecvDataDto {
    private byte[] bytes;
    private int count;
    private int ret = 0;
    public Integer getRet(){
        return ret;
    }
    public void setRet(Integer ret){
        this.ret = ret;
    }
    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
