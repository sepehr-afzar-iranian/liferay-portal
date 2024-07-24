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

package com.liferay.portal.security.audit.event.generators.user.management.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = {})
public class AuditMessageRoleAssociationHelper {

	public String getName(String associationClassName) {
		if (associationClassName.equals(Group.class.getName())) {
			return "GROUP";
		}

		if (associationClassName.equals(User.class.getName())) {
			return "USER";
		}

		return null;
	}

	public String getValue(String associationClassName, long associationClassP)
		throws PortalException {

		if (associationClassName.equals(Group.class.getName())) {
			return _getGroupName(associationClassP);
		}

		if (associationClassName.equals(User.class.getName())) {
			return _getUserName(associationClassP);
		}

		return null;
	}

	private String _getGroupName(long groupId) throws PortalException {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (!Objects.equals(group, null)) {
			return group.getDescriptiveName();
		}

		return null;
	}

	private String _getUserName(long userId) {
		User user = _userLocalService.fetchUser(userId);

		if (!Objects.equals(user, null)) {
			return user.getFullName();
		}

		return null;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}