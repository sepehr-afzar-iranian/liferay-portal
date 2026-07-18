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

package com.liferay.portal.security.content.security.policy.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Olivér Kecskeméty
 */
@ExtendedObjectClassDefinition(category = "security-tools")
@Meta.OCD(
	id = "com.liferay.portal.security.content.security.policy.internal.configuration.ContentSecurityPolicyConfiguration",
	localization = "content/Language", name = "content-security-policy"
)
public interface ContentSecurityPolicyConfiguration {

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		deflt = "/api/,/combo,/documents/,/image/,/layouttpl/,/o/,/webdav/",
		description = "content-security-policy-excluded-paths-help",
		name = "excluded-paths", required = false
	)
	public String[] excludedPaths();

	@Meta.AD(
		deflt = "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval' blob:; style-src 'self' 'unsafe-inline' blob:; img-src 'self' data: https: blob:; font-src 'self' data:; connect-src 'self'; frame-ancestors 'self'; form-action 'self'; base-uri 'self'; worker-src 'self' blob:; object-src 'none';",
		description = "content-security-policy-help",
		name = "content-security-policy", required = false
	)
	public String policy();

	@Meta.AD(deflt = "true", name = "report-only", required = false)
	public boolean reportOnly();

}