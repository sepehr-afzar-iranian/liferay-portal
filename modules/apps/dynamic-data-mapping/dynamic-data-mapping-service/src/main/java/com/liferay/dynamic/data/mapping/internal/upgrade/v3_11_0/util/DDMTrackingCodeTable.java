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

package com.liferay.dynamic.data.mapping.internal.upgrade.v3_11_0.util;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author	  Brian Wing Shun Chan
 * @generated
 */
public class DDMTrackingCodeTable {

	public static final String TABLE_NAME = "DDMTrackingCode";

	public static final Object[][] TABLE_COLUMNS = {
		{"mvccVersion", Types.BIGINT}, {"ctCollectionId", Types.BIGINT},
		{"companyId", Types.BIGINT}, {"formInstanceRecordId", Types.BIGINT},
		{"trackingCode", Types.VARCHAR}
	};

	public static final Map<String, Integer> TABLE_COLUMNS_MAP =
		new HashMap<String, Integer>();

	static {
		TABLE_COLUMNS_MAP.put("mvccVersion", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("ctCollectionId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("formInstanceRecordId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("trackingCode", Types.VARCHAR);
	}

	public static final String TABLE_SQL_CREATE =
		"create table DDMTrackingCode (mvccVersion LONG default 0 not null,ctCollectionId LONG default 0 not null,companyId LONG,formInstanceRecordId LONG not null,trackingCode VARCHAR(75) null,primary key (formInstanceRecordId, ctCollectionId))";

	public static final String TABLE_SQL_DROP = "drop table DDMTrackingCode";

	public static final String[] TABLE_SQL_ADD_INDEXES = {
			"create unique index IX_D1455B8D on DDMTrackingCode (trackingCode[$COLUMN_LENGTH:75$], ctCollectionId);"
	};

}
