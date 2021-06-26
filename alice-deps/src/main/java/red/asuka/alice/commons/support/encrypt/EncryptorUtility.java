package red.asuka.alice.commons.support.encrypt;

import red.asuka.alice.commons.exception.EXPF;
import red.asuka.alice.commons.support.DateUtility;
import red.asuka.alice.commons.support.ImageUtility;
import lombok.Setter;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: Tomonori
 * @Date: 2019/12/25 14:27
 * @Title: 加密工具类
 * @Desc: ↓ ↓ ↓ ↓ ↓
 * ----- PS: 在AutowiredConfiguration类中bean注入
 */
public class EncryptorUtility {

    protected static final Logger log = LoggerFactory.getLogger(EncryptorUtility.class);
    @Setter
    private String key;

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    public EncryptorUtility(String key) {
        this.key = key;
    }

    /**
     * 获取消息摘要，base64的话获取null
     * @param operation
     * @return
     */
    private MessageDigest getDigest(OPERATION operation) {
        switch (operation) {
            case MD2:
                return DigestUtils.getMd2Digest();
            case MD5:
                return DigestUtils.getMd5Digest();
            case SHA1:
                return DigestUtils.getSha1Digest();
            case SHA256:
                return DigestUtils.getSha256Digest();
            case SHA384:
                return DigestUtils.getSha384Digest();
            case SHA512:
                return DigestUtils.getSha512Digest();
            default:
                return null;
        }
    }

    public enum OPERATION {
        MD2("md2"),
        MD5("md5"),
        SHA1("sha1"),
        SHA256("sha256"),
        SHA384("sha384"),
        SHA512("sha512"),
        BASE64("base64");

        private String operation;

        OPERATION(String operation) {
            this.operation = operation;
        }

        public String toString() {
            return operation;
        }
    }

    /**
     * 字符串 - 使用key加盐加密
     * @param text
     * @param operation
     * @return
     */
    public String encrypt(String text, OPERATION operation) {
        return this.encryptWithSalt(text, key, operation);
    }

    public String encryptBase64(String text) {
        return this.encryptWithSalt(text, "alice", OPERATION.BASE64);
    }

    public String encryptWithSalt(String text, String salt, OPERATION operation) {
        if (StringUtils.isEmpty(text) || StringUtils.isEmpty(salt)) {
            return null;
        }

        MessageDigest digest = this.getDigest(operation);

        if (null != digest) {
            DigestUtils.updateDigest(digest, salt);
            return Hex.encodeHexString(digest.digest(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(text)));
        } else {
            Base64 base64 = new Base64();
            return base64.encodeAsString(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(text));
        }
    }

    /**
     * Ucs Md5
     * @param pwd
     * @return
     */
    public String UcsMD5(String pwd) throws NoSuchAlgorithmException {
        //用于加密的字符
        char md5String[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };

        //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
        byte[] btInput = pwd.getBytes();

        //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
        MessageDigest mdInst = MessageDigest.getInstance("MD5");

        //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
        mdInst.update(btInput);

        // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
        byte[] md = mdInst.digest();

        // 把密文转换成十六进制的字符串形式
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {   //  i = 0
            byte byte0 = md[i];  //95
            str[k++] = md5String[byte0 >>> 4 & 0xf];    //    5
            str[k++] = md5String[byte0 & 0xf];   //   F
        }

        //返回经过加密后的字符串
        return new String(str);
    }

    /**
     * InputStream - 使用key加盐加密
     * @param input
     * @param operation
     * @return
     */
    public String encrypt(InputStream input, OPERATION operation) throws IOException {
        return this.encryptWithSalt(input, key, operation);
    }

    public String encryptWithSalt(InputStream input, String salt, OPERATION operation) throws IOException {
        if (null == input || StringUtils.isEmpty(salt)) {
            return null;
        }

        MessageDigest digest = this.getDigest(operation);

        if (null != digest) {
            DigestUtils.updateDigest(digest, salt);
            DigestUtils.updateDigest(digest, input);

            return Hex.encodeHexString(digest.digest());
        } else {
            byte[] data = new byte[input.available()];
            input.read(data);

            Base64 base64 = new Base64();

            return base64.encodeAsString(data);
        }
    }

    /**
     * 普通加密（无盐）
     * @param text
     * @param operation
     * @return
     */
    public String encryptNoSalt(String text, OPERATION operation) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        switch (operation) {
            case MD2:
                return DigestUtils.md2Hex(text);
            case MD5:
                return DigestUtils.md5Hex(text);
            case SHA1:
                return DigestUtils.sha1Hex(text);
            case SHA256:
                return DigestUtils.sha256Hex(text);
            case SHA384:
                return DigestUtils.sha384Hex(text);
            case SHA512:
                return DigestUtils.sha512Hex(text);
            case BASE64:
                return new Base64().encodeAsString(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(text));
            default:
                return null;
        }
    }

    /**
     * InputStream - 普通加密（无盐）
     * @param input
     * @param operation
     * @return
     * @throws IOException
     */
    public String encryptNoSalt(InputStream input, OPERATION operation) throws IOException {
        if (null == input) {
            return null;
        }

        switch (operation) {
            case MD2:
                return DigestUtils.md2Hex(input);
            case MD5:
                return DigestUtils.md5Hex(input);
            case SHA1:
                return DigestUtils.sha1Hex(input);
            case SHA256:
                return DigestUtils.sha256Hex(input);
            case SHA384:
                return DigestUtils.sha384Hex(input);
            case SHA512:
                return DigestUtils.sha512Hex(input);
            case BASE64:
                byte[] data = new byte[input.available()];
                input.read(data);
                return new Base64().encodeAsString(data);
            default:
                return null;
        }
    }

    public String base64Decrypt(String text) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        return new String(this.decode(text), "utf-8");
    }

    private byte[] decode(String str) {
        return new Base64().decode(str);
    }

    public void base64Decrypt(String str, OutputStream out) {
        try {
            out.write(this.decode(str));
        } catch (IOException e) {
            log.error(EXPF.getExceptionMsg(e));
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    //TODO Auto-generated catch block
                    log.error(EXPF.getExceptionMsg(e));
                }
            }
        }
    }

    public String decrypt(String value) {
        try {
            return new String(decode(value), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public String decrypt(String value, String chart) {
        try {
            return new String(decode(value), chart);
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public String ucsEncrypt(String data) throws NoSuchAlgorithmException {
        return ucsSha1(ucsMd5Hex(data));
    }

    public String ucsMd5Hex(final String data) {
        return org.springframework.util.DigestUtils.md5DigestAsHex(data.getBytes(Charsets.UTF_8));
    }

    public String ucsSha1(String str) throws NoSuchAlgorithmException {
        return hash("SHA-1", str);
    }

    private String hash(String s, String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(s);
        byte[] bytes = md.digest(str.getBytes(Charsets.UTF_8));
        return toHex(bytes);
    }

    public String toHex(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length * 2);

        for(int i = 0; i < bytes.length; ++i) {
            ret.append(HEX_DIGITS[bytes[i] >> 4 & 15]);
            ret.append(HEX_DIGITS[bytes[i] & 15]);
        }

        return ret.toString();
    }

    public static String aesDecrypt(String content, String decryptKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
            byte[] decryptBytes = cipher.doFinal(new BASE64Decoder().decodeBuffer(content));
            return new String(decryptBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
//        String[] split = StringUtils.split("data:image/jpg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAMgArkDASIAAhEBAxEB/8QAHQAAAgICAwEAAAAAAAAAAAAAAAcGCAQFAQIDCf/EAF4QAAEDAwEFBAcEBggDAwgEDwECAwQABREGBxIhMUETUWFxCBQiMoGRoRUjQrFSYnKCksEWJDNDorLC0VNj8DRz0hclJkSDk6PhGDVVZJSks8MoRVR08Sc2RoS04v/EABoBAQEBAQEBAQAAAAAAAAAAAAACAQMEBQb/xAAxEQACAgEEAgAEBAYDAQEAAAAAAQIRMQMSIUEEURMiMmEFcYGxFEKRodHwI8HhUgb/2gAMAwEAAhEDEQA/ALU0UUUAUUUUAUUUUAUUUUAUUUUAUUUUAUUUE4GTQBRkd9RzW2srDoy2+vaiuDcRo5DaFcVukDOEJHEmq/3PW+v9tMp227P4L1l0xvhDtzcUW3FjkcrBx+4jJ7zQDU2l7ZtLaGS4w7J+0rv+CBEUFKz0C1cQj48fA0ubXB2obYl72oHnNJ6Pf9osxxuPvpxwAz7RB6l", ";base64,");
        String str = DateUtility.convertTimestamp(DateUtility.getPrevMonthTimestamp(), "yyyy-MM-dd");
//        str = DateUtility.convertTimestamp(DateUtility.getNextMonthTimestamp(), "yyyy-MM-dd");
//        str = DateUtility.calcDayDate(new Date(), 1, "yyyy-MM-dd");
//        str = DateUtility.calcDayDate(new Date(), -8, "yyyy-MM-dd");
//        Date date = new Date();
//        str = DateUtility.convertTimestamp(1577808000000L, "yyyy-MM-dd hh:mm:ss");
//        str = DateUtility.convertTimestamp(1640958499000L, "yyyy-MM-dd hh:mm:ss");
//        boolean a = DateUtility.isEffectiveDate(date, new Date(1577808000000L), new Date(1640958499000L));
//        str = StringUtility.getRandomStr(10);
//        Long currentZeroTime = DateUtility.currentZeroPointTimestamps();
//        String date = DateUtility.convertTimestamp(currentZeroTime, "yyyy-MM-dd HH:mm:ss");
//        String eA20lxsi6B3ZeYtudHYj2DrGlDQdBi2I = aesDecrypt("IOd4g6RQRQs7DKEgCdcfiw==", "eA20lxsi6B3ZeYtudHYj2DrGlDQdBi2I");
        String url = "https://ph-hbbd.knowniot.com/publichouse/ucs/sailmi-resource/oss/endpoint/file/MzYwZmFmMTAzOGJhNDg4MmFlN2RjYjY0ZTM0ODJhOGYuanBn";
        byte[] bytes = ImageUtility.compression(url, 1.5, 0.1f, 0.5f);
        String encode = ImageUtility.toBase64(bytes);
        System.out.println(str);
    }
}
