package org.atemsource.atem.utility.binding;

import org.atemsource.atem.api.type.EntityType;

public class PackageTypeFilter implements TypeFilter {
	private String includedPackage;

	@Override
	public boolean isExcluded(EntityType<?> entityType) {
		return !entityType.getCode().startsWith(includedPackage);
	}

	public String getIncludedPackage() {
		return includedPackage;
	}

	public void setIncludedPackage(String includedPackage) {
		this.includedPackage = includedPackage;
	}

}
