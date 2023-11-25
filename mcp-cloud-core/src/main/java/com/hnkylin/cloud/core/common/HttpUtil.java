package com.hnkylin.cloud.core.common;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;

public class HttpUtil {

    //从mc下载图片，保存在本地中
    public static File downLoadMcServerVmLogo(String downLoadUrl, String localFile) {
        File file = null;
        try {

            TrustManager[] tm = {new MyX509TrustManager()};//1.生成trustmanager数组
            SSLContext ssl = SSLContext.getInstance("TLS"); //2.得到sslcontext实例。SSL TSL 是一种https使用的安全传输协议
            ssl.init(null, tm, new SecureRandom());//初始化sslcontext
            SSLSocketFactory sslSocketFactory = ssl.getSocketFactory();//得到sslSocketFactory实例

            // 统一资源
            URL url = new URL(downLoadUrl);
            HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
                public boolean verify(String s, SSLSession sslsession) {
                    System.out.println("WARNING: Hostname is not matched for cert.");
                    return true;
                }
            };
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;

            httpsURLConnection.setHostnameVerifier(ignoreHostnameVerifier);
            httpsURLConnection.setSSLSocketFactory(sslSocketFactory);

            // 设定请求的方法，默认是GET
            httpsURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpsURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            httpsURLConnection.connect();
            // 文件大小
            int fileLength = httpsURLConnection.getContentLength();
            URLConnection con = url.openConnection();
            BufferedInputStream bin = new BufferedInputStream(httpsURLConnection.getInputStream());
            file = new File(localFile);
            if (!file.exists()) {
                OutputStream out = new FileOutputStream(file);
                int size = 0;
                int len = 0;
                byte[] buf = new byte[1024];
                while ((size = bin.read(buf)) != -1) {
                    len += size;
                    out.write(buf, 0, size);
                }
                out.flush();
                out.close();
            }
            bin.close();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            return file;
        }
    }
}
