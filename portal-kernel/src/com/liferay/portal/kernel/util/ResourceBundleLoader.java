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

package com.liferay.portal.kernel.util;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Carlos Sierra Andrés
 *
 * @deprecated As of Athanasius (7.3.x), replaced by {@link
 *             com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader}
 */
@Deprecated
@FunctionalInterface
@ProviderType
public interface ResourceBundleLoader {

	public ResourceBundle loadResourceBundle(Locale locale);

}