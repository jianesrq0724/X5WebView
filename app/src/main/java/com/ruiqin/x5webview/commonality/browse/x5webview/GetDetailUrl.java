package com.ruiqin.x5webview.commonality.browse.x5webview;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ruiqin on 2017/7/16.
 * 获取网页中的URL
 */

public class GetDetailUrl {

    static int conentLength = "55211918".length();

    static List<String> detailUrl = new ArrayList<>();


    static String subContent = "jianesrq0724/article/details/";

    static String sourceCode = "";

    public static void main(String args[]) {
        sourceCode = getPageSource("http://blog.csdn.net/jianesrq0724?viewmode=contents", "GBK");//获取源码
        while (sourceCode.contains(subContent)) {
            int beginIndex = sourceCode.indexOf(subContent) + subContent.length();
            int endIndex = beginIndex + conentLength;
            String tempDetailUrl = "\"" + sourceCode.substring(beginIndex, endIndex) + "\"";
            if (!detailUrl.contains(tempDetailUrl)) {
                detailUrl.add(tempDetailUrl);
            }
            sourceCode = sourceCode.substring(endIndex);
        }

        System.out.println(detailUrl);
    }

    /**
     * 获取URl的源码
     *
     * @param pageUrl
     * @param encoding
     * @return
     */
    public static String getPageSource(String pageUrl, String encoding) {
        StringBuffer sb = new StringBuffer();
        try {
            //构建一URL对象
            URL url = new URL(pageUrl);
            //使用openStream得到一输入流并由此构造一个BufferedReader对象
            BufferedReader in = new BufferedReader(new InputStreamReader(url
                    .openStream(), encoding));
            String line;
            //读取www资源
            while ((line = in.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            in.close();
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return sb.toString();
    }

}
