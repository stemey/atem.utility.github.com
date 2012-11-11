package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;

public class StringToLongConverter implements JavaConverter<String,Long>{

	@Override
	public Long convertAB(String a, TransformationContext ctx) {
		if (a==null) {
			return null;
			}else{
		return Long.parseLong(a);
		}
	}

	@Override
	public String convertBA(Long b, TransformationContext ctx) {
		if (b==null) {
			return null;
		}else{
		return b.toString();
		}
	}

}
