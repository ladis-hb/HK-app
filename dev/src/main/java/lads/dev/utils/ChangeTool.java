package lads.dev.utils;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2019-07-01
 */
public class ChangeTool {

    public static int isOdd(int num) {
        return num & 1;
    }

    //-------------------------------------------------------
    //Hex字符串转int
    public static int HexToInt(String inHex) {
        return Integer.parseInt(inHex, 16);
    }

    //-------------------------------------------------------
    //Hex字符串转byte
    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    //-------------------------------------------------------
    //1字节转2个Hex字符
    public static String Byte2Hex(Byte inByte) {
        return String.format("%02x", new Object[]{inByte}).toUpperCase();
    }

    //-------------------------------------------------------
    //字节数组转转hex字符串
    public static String ByteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte valueOf : inBytArr) {
            strBuilder.append(Byte2Hex(Byte.valueOf(valueOf)));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    //-------------------------------------------------------
    //字节数组转转hex字符串，可选长度
    public static String ByteArrToHex(byte[] inBytArr, int offset, int byteCount) {
        StringBuilder strBuilder = new StringBuilder();
        int j = byteCount;
        for (int i = 0; i < j; i++) {
            strBuilder.append(Byte2Hex(Byte.valueOf(inBytArr[i+offset])));
        }
        return strBuilder.toString();
    }

    //-------------------------------------------------------
    //把hex字符串转字节数组
    public static byte[] HexToByteArr(String inHex) {
        byte[] result;
        int hexlen = inHex.length();
        if (isOdd(hexlen) == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }


    public static int bytesToInt(byte[] bytes, int idx) {
        if(bytes.length < 4+idx) {
            return -1;
        }

        return bytes[idx+3] & 0xFF |
                (bytes[idx+2] & 0xFF) << 8 |
                (bytes[idx+1] & 0xFF) << 16 |
                (bytes[idx] & 0xFF) << 24;
    }
    public static Float bytesToFloat(byte[] b, int index) {

        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    public static String bytesToFloat(byte[] bytes) {
        if(bytes.length<4) {
            return null;
        }

        if(bytes[0] == 0x23) {
            return "";
        }

        int i = bytesToInt(bytes, 0);
        float f = Float.intBitsToFloat(i);

        java.text.DecimalFormat format = new DecimalFormat("#.##################");
        return format.format(f);

    }

    public static Integer bytesToShort(byte[] data) {

        if(data.length==0 || data.length>2) {
            return null;
        }

        byte[] bytes = new byte[2];
        if(data.length==1) {

            bytes[0]=0x00;
            bytes[1] = data[0];
        } else {
            bytes = data;
        }

        byte tmp = (byte)(bytes[0] & 0x80);

        int iRet=(int)((bytes[0] & 0x7f)<<8 | bytes[1]&0xff);
        if(tmp==((byte)0x80)) {
            iRet=~iRet;
        }
        return iRet;
    }

    public static Integer bytesToShort(byte[] data, int idx) {

        byte[] bytes = new byte[2];
        System.arraycopy(data, idx, bytes, 0, 2);

        byte tmp = (byte)(bytes[0] & 0x80);

        int iRet=(int)((bytes[0] & 0x7f)<<8 | bytes[1]&0xff);
        if(tmp==((byte)0x80)) {
            iRet=~iRet;
        }
        return iRet;
    }

    public static Integer bytesToUnsignedShort(byte[] data) {
        if(data.length==0 || data.length>2) {
            return -1;
        }

        byte[] bytes = new byte[2];
        if(data.length==1) {

            bytes[0]=0x00;
            bytes[1] = data[0];
        } else {
            bytes = data;
        }
        return  (int)(((bytes[0] & 0x00FF) << 8) | (bytes[1] & 0x00FF) ) ;
    }

    public static Integer bytesToUnsignedShort(byte[] data, int idx) {
        return  (int)(((data[idx] & 0x00FF) << 8) | (data[idx+1] & 0x00FF) ) ;
    }
}
