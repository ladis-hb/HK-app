package lads.dev.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class MyByteUtil {
	
	public static short bytesToShort(byte[] bytes, int idx) {
		if(bytes.length < 2+idx) {
			return -1;
		}
		
		return (short)((bytes[idx+1] & 0xFF) | ((bytes[idx] & 0xFF) << 8));
	}
	
	public static String bytesToShortStr(byte[] bytes, int idx) {
		if(bytes.length < 2+idx) {
			return "";
		}
		if(bytes[0] == 0x23) {
			return "";
		}
		short s = (short)((bytes[idx+1] & 0xFF) | ((bytes[idx] & 0xFF) << 8));
		return String.valueOf(s);
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
	
	public static byte[] shortToBytes(short a) {
		return new byte[]{     
		        (byte) ((a >> 8) & 0xFF),     
		        (byte) (a & 0xFF) 
		};
	}
	
	public static byte[] intToBytes(int a) {
		return new byte[]{
				(byte) ((a >> 24) & 0xFF),  
		        (byte) ((a >> 16) & 0xFF),     
		        (byte) ((a >> 8) & 0xFF),     
		        (byte) (a & 0xFF) 
		};
	}
	
	public static String bytesToHexString(byte[] src){   
        StringBuilder stringBuilder = new StringBuilder("");   
        if (src == null || src.length <= 0) {   
            return null;   
        }   
        for (int i = 0; i < src.length; i++) {   
            int v = src[i] & 0xFF;   
            String hv = Integer.toHexString(v);   
            if (hv.length() < 2) {   
                stringBuilder.append(0);   
            }   
            stringBuilder.append(hv);   
        }   
        return stringBuilder.toString();   
    }   
    
    public static byte[] hexStringToBytes(String hexString) {   
        if (hexString == null || hexString.equals("")) {   
            return null;   
        }   
        hexString = hexString.toUpperCase();   
        int length = hexString.length() / 2;   
        char[] hexChars = hexString.toCharArray();   
        byte[] d = new byte[length];   
        for (int i = 0; i < length; i++) {   
            int pos = i * 2;   
            d[i] = (byte) (hexcharToByte(hexChars[pos]) << 4 | hexcharToByte(hexChars[pos + 1]));   
        }   
        return d;   
    }
    
    private static byte hexcharToByte(char c) {   
    	return (byte) "0123456789ABCDEF".indexOf(c);   
    } 
    
    public static byte char2Byte(char c) {
    	return (byte)((int)c);
    }
    
    public static String printHexString(byte[] src){   
        StringBuilder stringBuilder = new StringBuilder("");   
        if (src == null || src.length <= 0) {   
            return null;   
        }   
        for (int i = 0; i < src.length; i++) {   
            int v = src[i] & 0xFF;   
            String hv = Integer.toHexString(v);   
            if (hv.length() < 2) {   
                stringBuilder.append(0);   
            }   
            stringBuilder.append(hv+":");   
        }   
        String s = stringBuilder.toString();
        if(s.length()>0) {
        	s = s.substring(0, s.length()-1);
        }
        return s;
    } 
    
    public static String byteToASCII(byte b) {
    	return new String(new byte[]{b});
    }
    
    public static String ladsAcRatio10Data(byte[] data) {
    	if(data[0]==0x23 && data[1]==0x23) {
    		return "";
    	}
    	
    	short s = bytesToShort(data, 0);
    	BigDecimal bDecimal = new BigDecimal(s);
    	return String.valueOf(s/10.0);
    }
    
    public static String ladsAcRatio1Data(byte[] data) {
    	if(data[0]==0x23) {
    		return "";
    	}
    	byte[] bytes = new byte[2];
    	bytes[0]=0x00;
    	bytes[1]=data[0];
    	short s = bytesToShort(bytes, 0);
    	return String.valueOf(s);
    }
    
    public static byte[] float2Byte(float f) {
    	int i = Float.floatToIntBits(f);
    	return intToBytes(i);
    }
    
    public static Float bytes2Float(byte[] b, int index) {
    	/*if(4+idx>bytes.length) {
    		return null;
    	}
    	
    	if(bytes[0] == 0x23) {
    		return null;
    	}
    	
    	int i = bytesToInt(bytes, idx);
    	return Float.intBitsToFloat(i);*/
    	
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
    
    public static String bytes2Float(byte[] bytes) {
    	if(bytes.length<4) {
    		return null;
    	}
    	
    	if(bytes[0] == 0x23) {
    		return "";
    	}
    	
    	int i = bytesToInt(bytes, 0);
    	float f = Float.intBitsToFloat(i);
    	
    	DecimalFormat format = new DecimalFormat("#.##################");
    	return format.format(f);
    	
    }
    
    //TODO 改善方法
    /**
     *	TODO 改善方法
     */
    public static Integer getSignedShort(byte[] data) {
    	//return (short)((data[1] & 0xFF) | ((data[0] & 0xFF) << 8));
    	//return (short) (((short) data[0] << 8) | ((short) data[1] & 0xff));
    	//byte tmp = (byte)(data[0] >> 7);
    	
    	if(data.length==0 || data.length>2) { 
    		return -1;
    	}
    	
    	if(data[0] == 0x23) {
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
    
    public static Integer getUnsignedShort(byte[] data) {
    	if(data.length==0 || data.length>2) { 
    		return -1;
    	}
    	
    	if(data[0] == 0x23) {
			return null;
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
    
    /**
     * 如果包含0x23，则返回null
     */
    public static Integer getIntSpecial(byte[] data) {
		if(data.length!=4) {	
			return null;
		}
		
		if(data[0]==0x23) {
			return null;
		}
		
		return data[3] & 0xFF |
				(data[2] & 0xFF) << 8 |
				(data[1] & 0xFF) << 16 |
				(data[0] & 0xFF) << 24;
	}
    
    public static String bytes2Ascii(byte[] bytes) {
    	return new String(bytes, 0, bytes.length);
    }
    
    public static void main(String[] args) {
    	/*byte[] data = new byte[2];
    	data[0]=(byte)0x7f; //0x7f,0xff用来测试 
    	data[1]=(byte)0xff;
		System.out.println(String.valueOf(getSignedShort(data)));
		System.out.println(String.valueOf(getUnsignedShort(data)));*/
    	
    	byte[] data = new byte[1];
    	data[0]=0x23;
    	System.out.println(String.valueOf(getSignedShort(data)));
		
    	/*
		DeviceBean deviceBean1 = new DeviceBean();
		deviceBean1.setDevid("111");
		deviceBean1.setInDb(true);
		MyCommunicationWrapper.mapDevice.put("111", deviceBean1);
		
		//DeviceBean deviceBean2 = MyCommunicationWrapper.mapDevice.get("111");
		//deviceBean2.setInDb(false);
		
		System.out.println(((DeviceBean)MyCommunicationWrapper.mapDevice.get("111")).isInDb());
		*/
	}
}
