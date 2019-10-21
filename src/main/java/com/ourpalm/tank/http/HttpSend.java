package com.ourpalm.tank.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;

import com.ourpalm.core.log.LogCore;

public class HttpSend {
	private static Logger logger = LogCore.runtime;

	/**
	 * 发送post 请求
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws IOException
	 */
	public static String sendPost(String url, String param) {
		HttpURLConnection conn;
		String result = "";
		try {
			URL requestUrl = new URL(url);
			conn = (HttpURLConnection) requestUrl.openConnection();
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			PrintWriter writer = new PrintWriter(conn.getOutputStream());
			writer.print(param);
			writer.flush();
			writer.close();
			String line;
			BufferedReader bufferedReader;
			StringBuilder sb = new StringBuilder();
			InputStreamReader streamReader = null;
			streamReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
			if (streamReader != null) {
				bufferedReader = new BufferedReader(streamReader);
				sb = new StringBuilder();
				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line);
				}
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 发送request请求(get方式)
	 * 
	 * @param address
	 * @param params
	 * @return
	 */
	public static String sendGet(String requestUrl) {
		String result = "";
		URLConnection conn = null;
		InputStream is = null;
		BufferedReader br = null;
		try {
			URL url;
			url = new URL(requestUrl);
			conn = url.openConnection();
			// 超时设置500毫秒
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();
			is = conn.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String line;
			while ((line = br.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error("发送请求失败,requrl = [{}],exception=[]", requestUrl, e);
			return result;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception e) {
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (Exception e) {
				} finally {
					is = null;
				}
			}

		}
		return result;
	}
}
