package com.ourpalm.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class RTSI {
	private static final String FILE_URL_PREFIX = "file:";
	private static final String JAR_URL_PREFIX = "jar:file:";
	private static final String JAR_URL_SEPARATOR = "!/";
	
	/*public static void main(String[] args){
		List<String> pkgList = new ArrayList<String>();
		pkgList.add("sacred.alliance.magic.gateway.message.request");
		Set<Class> clzSet = RTSI.findClass(pkgList, sacred.alliance.magic.core.Message.class);
		System.out.println(clzSet.size());
	}*/

	public static Set<Class> findClass(String pkg, Class tosubclass) {
		List<URL> urlList = getPkgResource(pkg);
		if (null == urlList) {
			return null;
		}
		Set<Class> clazzSet = new HashSet<Class>();
		for (URL url : urlList) {
			if (null == url) {
				continue;
			}
			File directory = new File(url.getFile());
			if (directory.isDirectory()) {
				//file system
				clazzSet.addAll(fromFileSystem(directory, pkg, tosubclass));
				continue;
			}
			//jar file
			try {
				clazzSet.addAll(fromJar(url, pkg, tosubclass));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return clazzSet;
	}

	public static Set<Class> findClass(List<String> pkgList, Class tosubclass) {
		Set<Class> clazzSet = new HashSet<Class>();
		for (String pkg : pkgList) {
			Set<Class> current = findClass(pkg, tosubclass);
			if (null != current) {
				clazzSet.addAll(current);
			}
		}
		return clazzSet;
	}

	 private static URL getURL(String classpathPart, String pckgname) {
		String urlStr;
		URL result;
		File classpathFile;
		File file;
		JarFile jarfile;
		Enumeration enm;
		String pckgnameTmp;

		result = null;
		urlStr = null;

		try {
			classpathFile = new File(classpathPart);
			// directory or jar?
			if (classpathFile.isDirectory()) {
				// does the package exist in this directory?
				file = new File(classpathPart + pckgname);
				if (file.exists()){
					urlStr = FILE_URL_PREFIX + classpathPart + pckgname;
				}
			} else {
				// is package actually included in jar?
				jarfile = new JarFile(classpathPart);
				enm = jarfile.entries();
				pckgnameTmp = pckgname.substring(1); // remove the leading "/"
				while (enm.hasMoreElements()) {
					if (enm.nextElement().toString().startsWith(pckgnameTmp)) {
						urlStr = JAR_URL_PREFIX + classpathPart + JAR_URL_SEPARATOR + pckgnameTmp;
						break;
					}
				}
			}
		} catch (Exception e) {
			// ignore
			e.printStackTrace();
		}

		// try to generate URL from url string
		if (urlStr != null) {
			try {
				result = new URL(urlStr);
			} catch (Exception e) {
				System.err.println("Trying to create URL from '" + urlStr
						+ "' generates this exception:\n" + e);
				result = null;
			}
		}
		return result;
	}
	 
	public static List<URL> getPkgResource(String pkgname){
		List<URL> value = getPkgResourceCp(pkgname);
		if(value.size() > 0){
			return  value ;
		}
		return getPkgResourceCl(pkgname) ;
	}
	
	/**
	 * @param pkgname
	 * @return
	 */
	public static List<URL> getPkgResourceCp(String pkgname) {
		List<URL> list = new ArrayList<URL>();
		if (!pkgname.startsWith("/")) {
			pkgname = "/" + pkgname;
		}
		pkgname = pkgname.replace('.', '/');
		StringTokenizer tok = new StringTokenizer(System
				.getProperty("java.class.path"), System
				.getProperty("path.separator"));
		while (tok.hasMoreTokens()) {
			String part = tok.nextToken();
			URL url = getURL(part, pkgname);
			if (null == url) {
				continue;
			}
			list.add(url);
		}
		return list ;
	}
	
	/**
	 * web�������سɹ�
	 * ��web����,�˷���������,classLoader.getResources(path)��ʱ�޷������Դ
	 * ԭ��δ֪
	 * @param pkgname
	 * @return
	 */
	public static List<URL> getPkgResourceCl(String pkgname) {
		String path = pkgname.replace('.', '/');
		List<URL> list = new ArrayList<URL>();
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		try {
			Enumeration<URL> resources = classLoader.getResources(path);
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				//System.out.println(resource.getFile());
				list.add(resource);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		return list;
	}

	public static boolean canUseClassAs(Class<?> tosubclass, Class<?> cl) {
		return tosubclass.isAssignableFrom(cl)
				&& ((cl.getModifiers() & Modifier.ABSTRACT) == 0);
	}

	public static Set<Class> fromFileSystem(File directory, String pkgname,
			Class tosubclass) {
		Set<Class> clazzSet = new HashSet<Class>();
		// Get the list of the files contained in the package
		String[] files = directory.list();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].endsWith(".class")) {
				continue;
			}
			// removes the .class extension
			String classname = files[i].substring(0, files[i].length() - 6);

			try {
				Class clazz = Class.forName(pkgname + "." + classname);
				if (canUseClassAs(tosubclass, clazz)) {
					// System.out.println(classname.substring(classname.lastIndexOf('.')+1));
					clazzSet.add(clazz);
				}
			} catch (ClassNotFoundException ex) {
				System.err.println(ex);
			}
		}
		return clazzSet;
	}

	public static Set<Class> fromJar(URL url, String pkgname, Class tosubclass)
			throws Exception {
		// It does not work with the filesystem: we must
		// be in the case of a package contained in a jar file.
		Set<Class> clazzSet = new HashSet<Class>();
		
		
		String urlStr = url.toString();
		int JAR_URL_SEPARATOR_INDEX = urlStr.indexOf(JAR_URL_SEPARATOR);
		String starts = urlStr.substring(JAR_URL_SEPARATOR_INDEX + JAR_URL_SEPARATOR.length()) ;
		JarFile jfile = new JarFile( new File(urlStr.substring(JAR_URL_PREFIX.length(),JAR_URL_SEPARATOR_INDEX)));
		
		Enumeration e = jfile.entries();
		while (e.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			String entryname = entry.getName();
			if (entryname.startsWith(starts)
					&& (entryname.lastIndexOf('/') <= starts.length())
					&& entryname.endsWith(".class")) {
				String classname = entryname.substring(0,
						entryname.length() - 6);
				if (classname.startsWith("/"))
					classname = classname.substring(1);
				classname = classname.replace('/', '.');
				if (!classname.startsWith(pkgname)) {
					continue;
				}
				try {
					Class clazz = Class.forName(classname);
					if (canUseClassAs(tosubclass, clazz)) {
						// System.out.println(classname.substring(classname.lastIndexOf('.')+1));
						clazzSet.add(clazz);
					}
				} catch (ClassNotFoundException ex) {
					System.err.println(ex);
				}
			}
		}
		return clazzSet;
	}
	
}
