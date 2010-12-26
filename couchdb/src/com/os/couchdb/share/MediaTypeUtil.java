package com.os.couchdb.share;

import java.util.HashMap;
import java.util.Map;

public class MediaTypeUtil {
	private static final String UNKNOWN_MEDIA_TYPE = "application/octet-stream";
	private static final Map<String, String> _suffixToMediaType = new HashMap<String, String>();
	static {
		_suffixToMediaType.put("jar", "application/java-archive");
		_suffixToMediaType.put("ser", "application/java-serialized-object");
		_suffixToMediaType.put("mdb", "application/msaccess");
		_suffixToMediaType.put("doc", "application/msword");
		_suffixToMediaType.put("dot", "application/msword");
		_suffixToMediaType.put("bin", "application/octet-stream");
		_suffixToMediaType.put("pdf", "application/pdf");
		_suffixToMediaType.put("ps", "application/postscript");
		_suffixToMediaType.put("ai", "application/postscript");
		_suffixToMediaType.put("eps", "application/postscript");
		_suffixToMediaType.put("rar", "application/rar");
		_suffixToMediaType.put("rss", "application/rss+xml");
		_suffixToMediaType.put("rtf", "application/rtf");
		_suffixToMediaType.put("xhtml", "application/xhtml+xml");
		_suffixToMediaType.put("xht", "application/xhtml+xml");
		_suffixToMediaType.put("xml", "application/xml");
		_suffixToMediaType.put("xsl", "application/xml");
		_suffixToMediaType.put("zip", "application/zip");
		_suffixToMediaType.put("kml", "application/vnd.google-earth.kml+xml");
		_suffixToMediaType.put("kmz", "application/vnd.google-earth.kmz");
		_suffixToMediaType.put("xul", "application/vnd.mozilla.xul+xml");
		_suffixToMediaType.put("xls", "application/vnd.ms-excel");
		_suffixToMediaType.put("xlt", "application/vnd.ms-excel");
		_suffixToMediaType.put("xlb", "application/vnd.ms-excel");
		_suffixToMediaType.put("ppt", "application/vnd.ms-powerpoint");
		_suffixToMediaType.put("pps", "application/vnd.ms-powerpoint");
		_suffixToMediaType.put("odc", "application/vnd.oasis.opendocument.chart");
		_suffixToMediaType.put("odb", "application/vnd.oasis.opendocument.database");
		_suffixToMediaType.put("odf", "application/vnd.oasis.opendocument.formula");
		_suffixToMediaType.put("odg", "application/vnd.oasis.opendocument.graphics");
		_suffixToMediaType.put("odi", "application/vnd.oasis.opendocument.image");
		_suffixToMediaType.put("odp", "application/vnd.oasis.opendocument.presentation");
		_suffixToMediaType.put("otp", "application/vnd.oasis.opendocument.presentation-template");
		_suffixToMediaType.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
		_suffixToMediaType.put("ots", "application/vnd.oasis.opendocument.spreadsheet-template");
		_suffixToMediaType.put("odt", "application/vnd.oasis.opendocument.text");
		_suffixToMediaType.put("7z", "application/x-7z-compressed");
		_suffixToMediaType.put("tgz", "application/x-gtar");
		_suffixToMediaType.put("taz", "application/x-gtar");
		_suffixToMediaType.put("gtar", "application/x-gtar");
		_suffixToMediaType.put("jnlp", "application/x-java-jnlp-file");
		_suffixToMediaType.put("js", "application/x-javascript");
		_suffixToMediaType.put("tar", "application/x-tar");
		_suffixToMediaType.put("au", "audio/basic");
		_suffixToMediaType.put("snd", "audio/basic");
		_suffixToMediaType.put("mpga", "audio/mpeg");
		_suffixToMediaType.put("mpega", "audio/mpeg");
		_suffixToMediaType.put("mp2", "audio/mpeg");
		_suffixToMediaType.put("mp3", "audio/mpeg");
		_suffixToMediaType.put("mp4a", "audio/mpeg");
		_suffixToMediaType.put("aif", "audio/x-aiff");
		_suffixToMediaType.put("aiff", "audio/x-aiff");
		_suffixToMediaType.put("aifc", "audio/x-aiff");
		_suffixToMediaType.put("wma", "audio/x-ms-wma");
		_suffixToMediaType.put("wav", "audio/x-wav");
		_suffixToMediaType.put("gif", "image/gif");
		_suffixToMediaType.put("jpeg", "image/jpeg");
		_suffixToMediaType.put("jpg", "image/jpeg");
		_suffixToMediaType.put("jpe", "image/jpeg");
		_suffixToMediaType.put("png", "image/png");
		_suffixToMediaType.put("svg", "image/svg+xml");
		_suffixToMediaType.put("svgz", "image/svg+xml");
		_suffixToMediaType.put("tiff", "image/tiff");
		_suffixToMediaType.put("tif", "image/tiff");
		_suffixToMediaType.put("bmp", "image/x-ms-bmp");
		_suffixToMediaType.put("xbm", "image/x-xbitmap");
		_suffixToMediaType.put("css", "text/css");
		_suffixToMediaType.put("csv", "text/csv");
		_suffixToMediaType.put("html", "text/html");
		_suffixToMediaType.put("htm", "text/html");
		_suffixToMediaType.put("shtml", "text/html");
		_suffixToMediaType.put("asc", "text/plain");
		_suffixToMediaType.put("txt", "text/plain");
		_suffixToMediaType.put("text", "text/plain");
		_suffixToMediaType.put("pot", "text/plain");
		_suffixToMediaType.put("java", "text/x-java");
		_suffixToMediaType.put("vcs", "text/x-vcalendar");
		_suffixToMediaType.put("vcf", "text/x-vcard");
		_suffixToMediaType.put("mpeg", "video/mpeg");
		_suffixToMediaType.put("mpg", "video/mpeg");
		_suffixToMediaType.put("mpe", "video/mpeg");
		_suffixToMediaType.put("mp4", "video/mp4");
		_suffixToMediaType.put("ogv", "video/ogg");
		_suffixToMediaType.put("qt", "video/quicktime");
		_suffixToMediaType.put("mov", "video/quicktime");
		_suffixToMediaType.put("avi", "video/x-msvideo");
	}

	public static String getMediaTypeForName(String name) {
		String mediaType = null;
		int pos = name.lastIndexOf('.');
		if (pos >= 0) {
			String ext = name.substring(pos + 1).toLowerCase();
			mediaType = _suffixToMediaType.get(ext);
		}
		if (mediaType == null) {
			return UNKNOWN_MEDIA_TYPE;
		} else {
			return mediaType;
		}
	}
}
