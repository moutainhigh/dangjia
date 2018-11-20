package com.dangjia.acg.common.pay.domain;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

public class AlipayConfig
{
  public static final String URL = "https://openapi.alipay.com/gateway.do";
  public static final String ALIPAY_APPID = "2017100909210538";
  public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi3ZjfN1FP5Hr/T3Qc4ts8HBnGIpp7pcMiuur+Atg47hpVnG9zNmgO6lXD3evD6ky+n7RRYVTtkn0sJVmKvLJvB7XLcm2nwovA8RRLkKfTw4aXJJskIlTWlwggOHRyiKZM9Z5gv4z22vhFrUy3cRkemJHQ5CInuzf5Xnx6/acXJWCI+ElgqNThUQTTriGlhyw2b5tOOPboMs/d/Yvpz1ibGux3xv4SwQ2kHqrLC5gdHDnTmaY4GY125QszAwk7jsHxJrW12rb5wfuWEcZNkrSa/sWHI5b68hJNTlhtmP5fg0kzbsiANkKtslDnDjEYKnLwQOX5nYtklvrv1LWeH2owwIDAQAB";
  public static String RSA_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCwhnl958FWDtz1hl+nxfrFCQRmWAj7wDO8J09U04HwJGTRhW9tfobtlLemO/18jPEHPgXmcCOAup8BvEKh1VPu/04J5P/X3j3vIY+YI1mz2K+I4C1CvjMxMx5QbA4aRO5LwNEwiRbzVuEM8Rs1x8XoTST8v1noI3IMgkj4BlFnbjqeBg1BLpoosz0mBvHWw7pqTTVdvHrLZz1IUfqK2/OyINx+M64FFuALRbeCdhhly2SnA3ukbjWysyMxYJlW/m0yF562rhao+F0EpuWQ9KweGcARjRT8b3DnISQcPAS1WbbgXYF2WKsH8RRxpVODY7xX0ay7c/CeFULcIv+iaRY5AgMBAAECggEAEKmAyjuX6jGY9avrV4C+fN6vBaIFpw2rVHU5bb5RI4Or6IL505H/1mkVNKGw+ZKsJYzbLbckAsCaxaUpfPEP7R04FoMBCzU1D19RmICttOYdG4RdgVFGr7wtflXUtvIOtCPNGxG1kt5+e6Q+d0MWESlG9nxxVhISb8/UZ2ThRivfGXlH9UI68MiAXlLQ6UOqhIcTzuCYGix8qNGhnI06aiidWVAagc9UNSUacoRTR2DLmEQzuYD5JVbLUXC+/V3tbXmvqgMjvUa3E4sL4mfRyhivAP/jK3FTpicS9AeLiOJgozSNkDlSv9Dgjpp2740yRmDQna7hX/8INCtNCOCDoQKBgQDpiyqJLRcEMXlKTca1YOUX7QpHhe69d+02pdGlptAJXmjGaOWcbfdDmS2savDmyuvUAHrdIYPKdKVtFHrDR6FEPLVt8hMRH3x5reZaNXPZ+AVuzybvTbJN250oJmLZeFts4gQVBFW+LKdH1kLG++k28YERo6o7mQldWku7HVuWBQKBgQDBf8OJb6APrMI4FkcOBkZvKlhaGtQnpo0HKPyMQTVCVZSEP/PxO1dQHUkkL6gMH6EeOMrVZgA+HwSpmvM6Cd0Awhq3B4f+0M+NKswnz444nlIul8c5/QQwZGDDItptrs1kqXab1skc+uvCh+RuJyG3/P1M8JOy6gwi9CnR4RvhpQKBgC5ocCBoLF2x8xc301exlHSnrtY9oA5YaXPliX5wYoCqGQnsSExIcc0mADR1378M7cXn/oI4qbaubTz6x9ZMQpiLs8XQR8esSRDo5JBidM4IghPd6C+/ccIU9OrIjfYG+6vsUcFXZi5vMyKMSiK+Zj57yRkXO7myFHFPgyMAl3V9AoGAM5JU0D2DAIp/3Sj62R8NCLXVhHZgEw1Nyxm+bbc3da2Pfsv4vVOeisizZMSa2lhXvdFPJh8gc24a9QVKTC6yy2/wZNXKBj0rBCni7b1g33A64RYs0CTCSA+Ixpl0dAVB0tFjvlc5lNK/oUJMvJsLXpk7/ZSlGOtOPsaqmriPkM0CgYEAyzv3qcOougYn+xCjmvS2bNQdCIlagj2q/9bF9uGcS62vhf4BBc0ZlyyKhpDxS5uirqTbMee3PEUY7FMutqLYesfRhszXswlwfK76mFTCVyQAQJnXHYYPxFtj8R5lapg/GbEF4mRuLFxe54Kbr8u7yG7IXw41vl9GGBgTtECZcGE=";
  public static final String SIGNTYPE = "RSA2";
  public static final String FORMAT = "json";
  public static final String CHARSET = "utf-8";
  public static final String notify_url = "";
  public static final String APPnotify = "http://....../alipay/APPnotify";
  public static final String OWnotify_url = "http://....../alipay/OWnotify";
  public static final String return_url = "https://www.xxx.com/";
  public static final String OWreturn_url = "https://www.xxx.com/";
  public static final String diamond_url = "http://www.xxx.com/GetRechergePrice";
  private static AlipayClient alipayClient = null;
  
  public static AlipayClient getAlipayClient()
  {
    if (alipayClient == null) {
      synchronized (AlipayConfig.class)
      {
        if (alipayClient == null) {
          alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2017100909210538", RSA_PRIVATE_KEY, "json", "utf-8", ALIPAY_PUBLIC_KEY, "RSA2");
        }
      }
    }
    return alipayClient;
  }
}
