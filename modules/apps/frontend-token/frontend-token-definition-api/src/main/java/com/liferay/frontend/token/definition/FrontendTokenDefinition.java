/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.frontend.token.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Iván Zaera
 */
@ProviderType
public interface FrontendTokenDefinition {

	public default Collection<FrontendTokenCategory>
		getFrontendTokenCategories() {

		return new ArrayList<>();
	}

	public default Collection<FrontendTokenMapping> getFrontendTokenMappings() {
		return new ArrayList<>();
	}

	public default Collection<FrontendToken> getFrontendTokens() {
		return new ArrayList<>();
	}

	public default Collection<FrontendTokenSet> getFrontendTokenSets() {
		return new ArrayList<>();
	}

	public String getJSON(Locale locale);

}