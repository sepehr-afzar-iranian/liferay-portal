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

package com.liferay.dynamic.data.mapping.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link DDMTrackingCode}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMTrackingCode
 * @generated
 */
public class DDMTrackingCodeWrapper
	extends BaseModelWrapper<DDMTrackingCode>
	implements DDMTrackingCode, ModelWrapper<DDMTrackingCode> {

	public DDMTrackingCodeWrapper(DDMTrackingCode ddmTrackingCode) {
		super(ddmTrackingCode);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("formInstanceRecordId", getFormInstanceRecordId());
		attributes.put("trackingCode", getTrackingCode());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long formInstanceRecordId = (Long)attributes.get(
			"formInstanceRecordId");

		if (formInstanceRecordId != null) {
			setFormInstanceRecordId(formInstanceRecordId);
		}

		String trackingCode = (String)attributes.get("trackingCode");

		if (trackingCode != null) {
			setTrackingCode(trackingCode);
		}
	}

	/**
	 * Returns the company ID of this ddm tracking code.
	 *
	 * @return the company ID of this ddm tracking code
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the ct collection ID of this ddm tracking code.
	 *
	 * @return the ct collection ID of this ddm tracking code
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the form instance record ID of this ddm tracking code.
	 *
	 * @return the form instance record ID of this ddm tracking code
	 */
	@Override
	public long getFormInstanceRecordId() {
		return model.getFormInstanceRecordId();
	}

	/**
	 * Returns the mvcc version of this ddm tracking code.
	 *
	 * @return the mvcc version of this ddm tracking code
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this ddm tracking code.
	 *
	 * @return the primary key of this ddm tracking code
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the tracking code of this ddm tracking code.
	 *
	 * @return the tracking code of this ddm tracking code
	 */
	@Override
	public String getTrackingCode() {
		return model.getTrackingCode();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this ddm tracking code.
	 *
	 * @param companyId the company ID of this ddm tracking code
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the ct collection ID of this ddm tracking code.
	 *
	 * @param ctCollectionId the ct collection ID of this ddm tracking code
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the form instance record ID of this ddm tracking code.
	 *
	 * @param formInstanceRecordId the form instance record ID of this ddm tracking code
	 */
	@Override
	public void setFormInstanceRecordId(long formInstanceRecordId) {
		model.setFormInstanceRecordId(formInstanceRecordId);
	}

	/**
	 * Sets the mvcc version of this ddm tracking code.
	 *
	 * @param mvccVersion the mvcc version of this ddm tracking code
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this ddm tracking code.
	 *
	 * @param primaryKey the primary key of this ddm tracking code
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the tracking code of this ddm tracking code.
	 *
	 * @param trackingCode the tracking code of this ddm tracking code
	 */
	@Override
	public void setTrackingCode(String trackingCode) {
		model.setTrackingCode(trackingCode);
	}

	@Override
	public Map<String, Function<DDMTrackingCode, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<DDMTrackingCode, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected DDMTrackingCodeWrapper wrap(DDMTrackingCode ddmTrackingCode) {
		return new DDMTrackingCodeWrapper(ddmTrackingCode);
	}

}