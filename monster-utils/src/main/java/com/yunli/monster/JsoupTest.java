package com.yunli.monster;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author zhouchao
 * @date 2019-01-17 14:53
 */
public class JsoupTest {

    /**
     * 将网络文件保存为本地文件的方法
     * @param destUrl 文件下载地址
     * @param fileName 文件名称
     * @throws IOException
     */
    public static void saveToFile(String destUrl, String fileName) throws IOException {
        byte[] buf = new byte[4096];
        int size = 0;
        // 建立链接
        URL url = new URL(destUrl);
        HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
        // 连接指定的资源
        httpconn.connect();
        // 获取网络输入流
        BufferedInputStream bis = new BufferedInputStream(httpconn.getInputStream());
        // 建立文件
        FileOutputStream fos = new FileOutputStream(fileName);
        System.out.println("正在获取链接[" + destUrl + "]的内容\n将其保存为文件[" + fileName + "]");
        // 保存文件
        while ((size = bis.read(buf)) != -1) {
            fos.write(buf, 0, size);
        }
        fos.close();
        bis.close();
        httpconn.disconnect();
    }

    public static void music() throws IOException {
        String root = "F:\\mp3\\";
        //获取整个网站的根节点，也就是html开头部分一直到结束
        Document document = Jsoup.connect("https://d.05.tn/车载音频/A（音乐）2018热歌新歌排行榜/").get();
        Elements elements = document.getElementsByClass("file");
        for (Element e : elements) {
            String href = e.getElementsByTag("a").attr("abs:href");
            String filename = e.attr("data-sort-name");
            saveToFile(href, root + filename);
            break;
        }
    }

    public static void main(String[] args) throws IOException {
        String url = "http://img.bigdatabugs.com/JAVA web模式设计之道@www.bigDataBugs.com.pdf";
        String root = "F:\\pdf\\JAVA web模式设计之道.pdf";
        saveToFile(url,root);
    }

}


