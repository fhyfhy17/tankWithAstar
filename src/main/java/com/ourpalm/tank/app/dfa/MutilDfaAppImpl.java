package com.ourpalm.tank.app.dfa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;

import com.ourpalm.core.log.LogCore;
import com.ourpalm.tank.domain.CharNode;

public class MutilDfaAppImpl implements MutilDfaApp{
	private static final Logger logger = LogCore.runtime;
	
	private static final String ENCODING = "UTF-8";
	
	private String path;
	
	private CharNode root;
	private int maxDeep;
	
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void start() {
		loadSensitiveWord();
	}
	
	private void loadSensitiveWord() {
		try {
			Set<String> keyWordSet = readSensitiveWordFile();
			
			root = new CharNode("r".charAt(0));
			String key = null;   
			int count = 0;
			CharNode end = null;
			
	        Iterator<String> iterator = keyWordSet.iterator();  
	        while(iterator.hasNext()){  
	            key = iterator.next();    //关键字  
	            count = key.length();
	            end = root;
	            
	            for(int i = 0; i < count; i++){  
	                char keyChar = key.charAt(i);       //转换成char型  
	                CharNode node = end.get(keyChar);       //获取  
	                end = node;
	            }  
	            
	            maxDeep = Math.max(maxDeep, count);
	            
	            end.setIsEnd();
	        }  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Set<String> matchSensitiveWord(String txt){  
		Set<String> set = new HashSet<>();
        char word = 0;  
        CharNode end  = null;
        txt = txt.trim().toLowerCase();		//全已小写匹配
        for(int i = 0, l = txt.length(); i < l; i++){  
            word = txt.charAt(i);  
            end = root.match(word);     //获取指定key  
            
            if(end == null)
            	continue;
            
            if(end.isEnd()) {
            	set.add(word +"");
            }
            
            for(int j = 1, ll = Math.min(maxDeep, l - i); j < ll; j++) {
            	end = end.match(txt.charAt(i + j));
            	if(end != null){     //存在，则判断是否为最后一个  
 	                if(end.isEnd()){       //如果为最后一个匹配规则,结束循环，返回匹配标识数  
 	                	set.add(txt.substring(i, i+j+1));
 	                }  
 	            } else {
 	            	break;
 	            }
            }
        }  
        return set;  
	 }  
	
	private Set<String> readSensitiveWordFile() throws Exception {
		Set<String> set = null;
		
		ClassPathResource res = new ClassPathResource(path);
		File file = res.getFile();
		InputStreamReader read = new InputStreamReader(new FileInputStream(file), ENCODING);
		BufferedReader br = null;
		try {
			set = new HashSet<String>();
			br = new BufferedReader(read);
			String txt = null;
			while((txt = br.readLine()) != null){    //读取文件，将文件内容放入到set中
				if(txt == null || txt.equals("")) {
					continue;
				}
				set.add(txt.trim().toLowerCase());
		    }
		} catch (Exception e) {
			logger.error("加载敏感词库发生异常...", e);
		}finally{
			if(br != null){
				br.close();
			}
			read.close();     //关闭文件流
		}
		return set;
	}

	@Override
	public void stop() {
		
	}
	
	
	
}
