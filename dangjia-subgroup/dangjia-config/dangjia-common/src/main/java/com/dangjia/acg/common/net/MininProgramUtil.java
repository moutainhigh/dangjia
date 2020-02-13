package com.dangjia.acg.common.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.http.HttpUtil;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ruking.Cheng
 * @descrilbe 小程序网络请求封装
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2020/1/4 1:44 PM
 */
public class MininProgramUtil {
    private static final String SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String APP_ID = "wx17091542223c15eb";//小程序 appId
    private static final String SECRET = "d0827c2694273dd4835582457d9b17a4";//小程序 appSecret
    private static final String GRANT_TYPE = "authorization_code";//授权类型，此处只需填写 authorization_code

    public static JSONObject jscode2session(String code){
        String param = "appid=" + APP_ID + "&secret=" + SECRET + "&js_code=" + code + "&grant_type=" + GRANT_TYPE;
        //发起服务器请求
        String result = HttpUtil.httpRequest(SESSION_URL, "GET", param);
        return JSON.parseObject(result);
    }

    public static Map<String,String> getPhone(String encrypted, String iv, String sessionkey) {
        try {
            // 解密
            byte[] encrypData = Base64Utils.decodeFromString(encrypted);
            byte[] ivData = Base64Utils.decodeFromString(iv);
            byte[] sessionKey = Base64Utils.decodeFromString(sessionkey);
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivData);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(sessionKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            String resultString = new String(cipher.doFinal(encrypData), StandardCharsets.UTF_8);
            JSONObject object = JSONObject.parseObject(resultString);
            // 拿到手机号码
            Map<String,String> map = new HashMap<>();
            map.put("phone",object.getString("phoneNumber"));
            map.put("unionid",object.getString("unionId"));
            return map;
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            JSONObject object = jscode2session("023b0ag90fkmTz1PCgf909ILf90b0agj");
            System.out.println("=====openid========" + object.getString("openid"));
            System.out.println("=====session_key========" + object.getString("session_key"));
            System.out.println("=====unionid========" + object.getString("unionid"));
            System.out.println("=====errcode========" + object.getInteger("errcode"));
            System.out.println("=====errmsg========" + object.getString("errmsg"));
        } catch (Exception e) {
            System.out.println("=====Exception========" + e.getMessage());
        }
        getPhone("1G0zw02unBKYhCHMMqS8Gno2AiukQ0H1reak4v/aFWTNkup5AAUEXjlFEviAsKysFRwWa5fkzhOdpgBkvUlBt0l7c8hsinmBylvdTC0TpOUq9tYCcEdF6O2EvcIj73P+NmxNc6CnUQvjkhFZ8PGXr6Co8RT6N7SyZEj7TxoH4PT/tlNFre3Ss4h272vOI5RtWtBqIkurEk/8YziOoNVCt3XlC15+XgkagUWef1ZNBoFDVpISHgY4Uhobio37uQteRTuDqClrsg6ds4fwBHHFGeJ8OLXFuahA015jcBvE2ErU8pM5JbyCiHryXrfJnHk5UK/fHZn4wy88gnj5jy7ZFujFQ/Zu4HpTuRjL/3BmyCYGNdaKZew/dRvuB8sqLoBjCDojIT5CpaVv7rkx1/d1iW4tbdhjTuOBcCnYIlzEzQgCGx2w8ypvmkbWNzanKfXjufj8vq4vlQZAwDEAsazVVAsKMm0APwT1uPHiLkBdMdy93yK0A3bEjI8QjgRhg85XMoX2g1fVzy0EwsIfsRJgGQ==",
                "CfzCvS4WiVhjEpivbOMSWQ==",
                "nY+Tk26S+0LN6LhOJ1UbUQ==");
    }
}
