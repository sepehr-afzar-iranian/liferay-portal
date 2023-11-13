package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util;


import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalServiceUtil;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import java.util.List;

@Component(immediate = true, service = DDMFormUniqueFieldEditChecker.class)
public class DDMFormUniqueFieldEditChecker {

	public void Check(
		ActionRequest actionRequest, DDMFormFieldValue currentValue)
		throws PortalException {

		long formInstanceRecordId = ParamUtil.getLong(
			actionRequest, "formInstanceRecordId");

		String currentReference = currentValue.getFieldReference();

		DDMFormInstanceRecord ddmFormInstanceRecord =
			DDMFormInstanceRecordLocalServiceUtil.
				getDDMFormInstanceRecord(formInstanceRecordId);

		List<DDMFormFieldValue> ddmFormInstanceRecords =
			ddmFormInstanceRecord.getDDMFormValues().
				getDDMFormFieldValues();

		String currentValue_ = String.valueOf(currentValue.getValue().
			getValues().values().toArray()[0]);

		String fieldName = "";
		boolean stillUnique = true;

		for (DDMFormFieldValue fieldValue : ddmFormInstanceRecords) {

			String oldValue = String.valueOf(fieldValue.getValue().
				getValues().values().toArray()[0]);
			fieldName = fieldValue.getFieldReference();
			if (!currentValue_.equals(oldValue) && fieldName.equals(
				currentReference)) {
				stillUnique = false;
				break;
			}
		}

		if (!stillUnique && _ddmFormUniqueFieldChecker.Check(
			currentValue_, fieldName)) {
			throw new PortalException("This Value is already used.");
		}
	}

	@Reference
	private DDMFormUniqueFieldChecker _ddmFormUniqueFieldChecker;
}
