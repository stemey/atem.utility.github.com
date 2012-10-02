package org.atemsource.atem.utility.doc.html;

public class TypeCodeToUrlConverter {
	private String basePath="doc";

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getUrl(String typeCode) {
		return "/"+basePath + typeCode.replace('.', '/') + ".html";
	}
}
