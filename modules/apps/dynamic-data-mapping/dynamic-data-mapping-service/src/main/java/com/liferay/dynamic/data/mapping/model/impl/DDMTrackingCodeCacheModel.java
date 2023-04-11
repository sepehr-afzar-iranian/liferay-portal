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

package com.liferay.dynamic.data.mapping.model.impl;

import com.liferay.dynamic.data.mapping.model.DDMTrackingCode;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing DDMTrackingCode in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class DDMTrackingCodeCacheModel
	implements CacheModel<DDMTrackingCode>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DDMTrackingCodeCacheModel)) {
			return false;
		}

		DDMTrackingCodeCacheModel ddmTrackingCodeCacheModel =
			(DDMTrackingCodeCacheModel)object;

		if ((formInstanceRecordId ==
				ddmTrackingCodeCacheModel.formInstanceRecordId) &&
			(mvccVersion == ddmTrackingCodeCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, formInstanceRecordId);

		return HashUtil.hash(hashCode, mvccVersion);
	}

	@Override
	public long getMvccVersion() {
		return mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		this.mvccVersion = mvccVersion;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(11);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", formInstanceRecordId=");
		sb.append(formInstanceRecordId);
		sb.append(", trackingCode=");
		sb.append(trackingCode);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public DDMTrackingCode toEntityModel() {
		DDMTrackingCodeImpl ddmTrackingCodeImpl = new DDMTrackingCodeImpl();

		ddmTrackingCodeImpl.setMvccVersion(mvccVersion);
		ddmTrackingCodeImpl.setCtCollectionId(ctCollectionId);
		ddmTrackingCodeImpl.setCompanyId(companyId);
		ddmTrackingCodeImpl.setFormInstanceRecordId(formInstanceRecordId);

		if (trackingCode == null) {
			ddmTrackingCodeImpl.setTrackingCode("");
		}
		else {
			ddmTrackingCodeImpl.setTrackingCode(trackingCode);
		}

		ddmTrackingCodeImpl.resetOriginalValues();

		return ddmTrackingCodeImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();

		companyId = objectInput.readLong();

		formInstanceRecordId = objectInput.readLong();
		trackingCode = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(formInstanceRecordId);

		if (trackingCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(trackingCode);
		}
	}

	public long mvccVersion;
	public long ctCollectionId;
	public long companyId;
	public long formInstanceRecordId;
	public String trackingCode;

}