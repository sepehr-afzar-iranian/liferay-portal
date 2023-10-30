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

import {ClayCheckbox, ClayInput} from '@clayui/form';
import classNames from 'classnames';
import React, {useEffect, useState} from 'react';

import {FieldBase} from '../FieldBase/ReactFieldBase.es';
import {setJSONArrayValue} from '../util/setters.es';
import {sumFormFieldsValues} from '../util/sumFormFields';

const Switcher = ({
	checked,
	disabled,
	inline,
	label,
	name,
	onBlur,
	onChange,
	onFocus,
	value,
}) => (
	<div
		className={classNames('lfr-ddm-form-field-checkbox-switch', {
			'lfr-ddm-form-field-checkbox-switch-inline': inline,
		})}
	>
		<label className="simple-toggle-switch toggle-switch">
			<input
				checked={checked}
				className="toggle-switch-check"
				disabled={disabled}
				name={name}
				onBlur={onBlur}
				onChange={onChange}
				onFocus={onFocus}
				type="checkbox"
				value={value}
			/>
			<span aria-hidden="true" className="toggle-switch-bar">
				<span className="toggle-switch-handle"></span>
			</span>
			<span className="toggle-switch-label">{label}</span>
		</label>
	</div>
);

const CheckboxMultiple = ({
	amountValues,
	disabled,
	inline,
	isSwitcher,
	name,
	onBlur,
	onChange,
	onFocus,
	options,
	portletNamespace,
	predefinedValue,
	priceField,
	value: initialValue,
}) => {
	const [value, setValue] = useState(initialValue);
	const [amountValue, setAmountValue] = useState(0);

	const displayValues = value && value.length > 0 ? value : predefinedValue;
	const Toggle = isSwitcher ? Switcher : ClayCheckbox;
	useEffect(() => {
		if (typeof portletNamespace !== 'undefined') {
			sumFormFieldsValues(portletNamespace);
		}
	}, [amountValue, portletNamespace]);

	const handleChange = (event) => {
		const {target} = event;
		const newValue = value.filter(
			(currentValue) => currentValue !== target.value
		);
		const currentAmountValue = parseInt(
			target.getAttribute('amountValue'),
			10
		);
		if (target.checked) {
			newValue.push(target.value);
			setAmountValue(amountValue + currentAmountValue);
		}
		else {
			setAmountValue(amountValue - currentAmountValue);
		}

		setValue(newValue);
		onChange(event, newValue);
	};

	return (
		<div className="lfr-ddm-checkbox-multiple">
			{options.map((option, index) => (
				<Toggle
					amountValue={amountValues.split(',')[index]}
					checked={displayValues.includes(option.value)}
					disabled={disabled}
					inline={inline}
					key={option.value}
					label={option.label}
					name={name}
					onBlur={onBlur}
					onChange={handleChange}
					onFocus={onFocus}
					value={option.value}
				/>
			))}
			<ClayInput
				data-price-field={priceField ? 'price-field' : ''}
				data-price-value={amountValue}
				type="hidden"
				value={value}
			/>
		</div>
	);
};

const Main = ({
	inline,
	name,
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
	onBlur,
	onChange,
	onFocus,
	predefinedValue,
	readOnly,
	showAsSwitcher = true,
	value,
	priceField,
	portletNamespace,
	amountValues,
	...otherProps
}) => (
	<FieldBase name={name} readOnly={readOnly} {...otherProps}>
		<CheckboxMultiple
			amountValues={amountValues}
			disabled={readOnly}
			inline={inline}
			isSwitcher={showAsSwitcher}
			name={name}
			onBlur={onBlur}
			onChange={onChange}
			onFocus={onFocus}
			options={options}
			portletNamespace={
				typeof portletNamespace !== 'undefined' ? portletNamespace : ''
			}
			predefinedValue={setJSONArrayValue(predefinedValue)}
			priceField={priceField}
			value={setJSONArrayValue(value)}
		/>
	</FieldBase>
);

Main.displayName = 'CheckboxMultiple';

export default Main;
