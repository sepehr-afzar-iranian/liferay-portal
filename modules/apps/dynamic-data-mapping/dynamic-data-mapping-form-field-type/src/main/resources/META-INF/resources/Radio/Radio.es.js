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

import {ClayInput, ClayRadio} from '@clayui/form';
import React, {useEffect, useMemo, useState} from 'react';

import {FieldBase} from '../FieldBase/ReactFieldBase.es';
import {useSyncValue} from '../hooks/useSyncValue.es';
import {setJSONArrayValue} from '../util/setters.es';
import {sumFormFieldsValues} from '../util/sumFormFields';

const Radio = ({
	options = [
		{
			label: 'Option 1',
			value: 'option1',
		},
		{
			label: 'Option 2',
			value: 'option2',
		},
	],
	inline,
	name,
	onBlur,
	onChange,
	onFocus,
	predefinedValue,
	readOnly: disabled,
	value: initialValue,
	priceField,
	portletNamespace,
	amountValues,
	...otherProps
}) => {
	let amountValuesArray = [0];
	if (typeof amountValues !== 'undefined') {
		amountValuesArray = amountValues.split(',');
	}

	const [amountValue, setAmountValue] = useState(0);
	useEffect(() => {
		if (typeof portletNamespace !== 'undefined') {
			sumFormFieldsValues(portletNamespace);
		}
	}, [amountValue, portletNamespace]);

	const predefinedValueMemo = useMemo(() => {
		const predefinedValueJSONArray =
			setJSONArrayValue(predefinedValue) || [];

		return predefinedValueJSONArray[0];
	}, [predefinedValue]);

	const [currentValue, setCurrentValue] = useSyncValue(
		initialValue ? initialValue : predefinedValueMemo
	);

	return (
		<FieldBase {...otherProps} name={name} readOnly={disabled}>
			<div className="ddm-radio" onBlur={onBlur} onFocus={onFocus}>
				{options.map((option, index) => (
					<ClayRadio
						checked={currentValue === option.value}
						data-price-field={priceField ? 'price-field' : ''}
						data-price-value={priceField ? amountValue : ''}
						disabled={disabled}
						inline={inline}
						key={option.value}
						label={option.label}
						name={name}
						onChange={(event) => {
							setCurrentValue(option.value);
							setAmountValue(amountValuesArray[index]);
							onChange(event);
						}}
						value={option.value}
					/>
				))}
			</div>
			<ClayInput name={name} type="hidden" value={currentValue} />
		</FieldBase>
	);
};

export default Radio;
