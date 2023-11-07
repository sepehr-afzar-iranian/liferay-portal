package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util;


import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException;
import com.liferay.portal.kernel.exception.PortalException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = DDMFormUniqueFieldSubmitChecker.class)
public class DDMFormUniqueFieldSubmitChecker {

	public void Check(DDMFormFieldValue fieldValue)
		throws PortalException {

		String currentValue = String.valueOf(fieldValue.getValue().getValues()
			.values().toArray()[0]);

		String fieldReference = fieldValue.getFieldReference();

		String fieldName = fieldValue.getDDMFormField().getLabel().getValues().
			values().toArray()[0].toString();


		boolean isUnique = _ddmFormUniqueFieldChecker.Check(
			currentValue, fieldReference);

		if (isUnique) {
			throw new DDMFormValuesValidationException.UniqueValue(
				fieldName, currentValue);
		}
	}

	@Reference
	private DDMFormUniqueFieldChecker _ddmFormUniqueFieldChecker;
}
