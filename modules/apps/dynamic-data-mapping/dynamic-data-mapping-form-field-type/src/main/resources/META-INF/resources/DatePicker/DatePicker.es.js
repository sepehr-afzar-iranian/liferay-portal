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

import ClayDatePicker from '@clayui/date-picker';
import {ClayInput} from '@clayui/form';
import jalali from 'jalali-moment';
import moment from 'moment/min/moment-with-locales';
import React, {useEffect, useMemo, useRef, useState} from 'react';
import DatePicker from 'react-modern-calendar-datepicker';
import {createAutoCorrectedDatePipe} from 'text-mask-addons';
import {createTextMaskInputElement} from 'text-mask-core';

import {FieldBase} from '../FieldBase/ReactFieldBase.es';
import {useSyncValue} from '../hooks/useSyncValue.es';

import './DatePicker.scss';

const DIGIT_REGEX = /\d/i;
const LETTER_REGEX = /[a-z]/i;
const LETTER_DIGIT_REGEX = /[A-Z0-9]/gi;
const NOT_LETTER_REGEX = /[^a-z]/gi;
const YEARS_INDEX = 6;

const getDateMask = (dateDelimiter, dateFormat) => {
	const lastSymbol = dateFormat.slice(-1).match(NOT_LETTER_REGEX);

	dateFormat = lastSymbol ? dateFormat.slice(0, -1) : dateFormat;

	return dateFormat
		.split(dateDelimiter)
		.map((item) => {
			let currentFormat;

			if (item === 'YYYY') {
				currentFormat = 'yyyy';
			}
			else if (item === 'DD') {
				currentFormat = 'dd';
			}
			else {
				currentFormat = 'MM';
			}

			return currentFormat;
		})
		.join(dateDelimiter);
};

const getDelimiter = (dateFormat) => {
	let dateDelimiter = '/';

	if (dateFormat.indexOf('.') != -1) {
		dateDelimiter = '.';
	}

	if (dateFormat.indexOf('-') != -1) {
		dateDelimiter = '-';
	}

	return dateDelimiter;
};

const getLocaleDateFormat = (locale, format = 'L') => {
	moment.locale(locale);

	return moment.localeData().longDateFormat(format);
};

const getMaskByDateFormat = (format) => {
	const mask = [];

	for (let i = 0; i < format.length; i++) {
		if (LETTER_REGEX.test(format[i])) {
			mask.push(DIGIT_REGEX);
		}
		else {
			mask.push(`${format[i]}`);
		}
	}

	return mask;
};

const getDateFormat = (locale) => {
	const dateFormat = getLocaleDateFormat(locale);
	const inputMask = getMaskByDateFormat(dateFormat);
	const dateDelimiter = getDelimiter(inputMask);

	return {
		dateMask: getDateMask(dateDelimiter, dateFormat),
		inputMask,
	};
};

const getInitialMonth = (value) => {
	if (moment(value).isValid()) {
		return moment(value).toDate();
	}

	return moment().toDate();
};

const getInitialValue = (
	defaultLanguageId,
	date,
	locale,
	formatInEditingLocale
) => {
	if (typeof date === 'string' && date.indexOf('_') === -1 && date !== '') {
		if (formatInEditingLocale) {
			return moment(date, [
				getLocaleDateFormat(defaultLanguageId),
				'YYYY-MM-DD',
			]).format(getLocaleDateFormat(defaultLanguageId));
		}

		return moment(date, [
			getLocaleDateFormat(defaultLanguageId),
			'YYYY-MM-DD',
		]).format(getLocaleDateFormat(defaultLanguageId));
	}

	return date;
};

const getValueForHidden = (value, locale = 'en_US') => {
	const momentLocale = moment().locale(locale);

	const momentLocaleFormatted = momentLocale.localeData().longDateFormat('L');

	const newMoment = moment(value, momentLocaleFormatted, true);

	if (newMoment.isValid()) {
		return newMoment.format('YYYY-MM-DD');
	}

	return '';
};

const LiferayDatePicker = ({
	disabled,
	formatInEditingLocale,
	locale,
	localizedValue: localizedValueInitial = {},
	name,
	onChange,
	spritemap,
	value: initialValue,
}) => {
	const defaultLanguageId = 'en_US';

	const inputRef = useRef(null);
	const maskInstance = useRef(null);

	const [expanded, setExpand] = useState(false);

	const [localizedValue, setLocalizedValue] = useState(localizedValueInitial);

	const initialValueMemoized = useMemo(
		() =>
			getInitialValue(
				defaultLanguageId,
				initialValue,
				locale,
				formatInEditingLocale
			),
		[defaultLanguageId, formatInEditingLocale, initialValue, locale]
	);

	const [value, setValue] = useSyncValue(initialValueMemoized);
	const [years, setYears] = useState(() => {
		const currentYear = new Date().getFullYear();

		return {
			end: currentYear + 5,
			start: currentYear - 5,
		};
	});

	const {dateMask, inputMask} = getDateFormat(defaultLanguageId);

	useEffect(() => {
		if (inputRef.current && inputMask && dateMask) {
			maskInstance.current = createTextMaskInputElement({
				guide: true,
				inputElement: inputRef.current,
				keepCharPositions: true,
				mask: inputMask,
				pipe: createAutoCorrectedDatePipe(dateMask.toLowerCase()),
				showMask: true,
			});

			const currentValue = localizedValue[locale];

			if (currentValue) {
				inputRef.current.value =
					currentValue.includes('/') ||
					currentValue.includes('.') ||
					(currentValue.includes('-') && currentValue.includes('_'))
						? currentValue
						: moment(currentValue).format(dateMask.toUpperCase());
			}
			else if (initialValueMemoized) {
				var year = parseInt(
					initialValueMemoized.substr(YEARS_INDEX),
					10
				);

				const date = moment(initialValueMemoized);

				if (year <= 50) {
					date.subtract(2000, 'years');
				}
				else if (year > 50 && year < 100) {
					date.subtract(1900, 'years');
				}

				inputRef.current.value = date.format(dateMask.toUpperCase());
			}
			else {
				inputRef.current.value = '';
			}

			if (
				inputRef.current.value.match(LETTER_DIGIT_REGEX) ||
				inputRef.current.value === ''
			) {
				maskInstance.current.update(inputRef.current.value);
			}
		}
	}, [
		dateMask,
		inputMask,
		inputRef,
		initialValueMemoized,
		localizedValue,
		locale,
	]);

	const handleNavigation = (date) => {
		const currentYear = date.getFullYear();

		setYears({
			end: currentYear + 5,
			start: currentYear - 5,
		});
	};

	return (
		<>
			<input
				aria-hidden="true"
				id={name + '_fieldDetails'}
				name={name}
				type="hidden"
				value={getValueForHidden(value)}
			/>
			<ClayDatePicker
				aria-labelledby={name + '_fieldDetails'}
				dateFormat={dateMask}
				disabled={disabled}
				expanded={expanded}
				initialMonth={getInitialMonth(value)}
				onExpandedChange={(expand) => {
					setExpand(expand);
				}}
				onInput={(event) => {
					maskInstance.current.update(event.target.value);
					setLocalizedValue({
						...localizedValue,
						[defaultLanguageId]: event.target.value,
					});
				}}
				onNavigation={handleNavigation}
				onValueChange={(value, eventType) => {
					setLocalizedValue({
						...localizedValue,
						[defaultLanguageId]: value,
					});

					setValue(value);

					if (eventType === 'click') {
						setExpand(false);
						inputRef.current.focus();
					}

					if (
						!value ||
						value === maskInstance.current.state.previousPlaceholder
					) {
						return onChange('');
					}

					if (
						moment(
							value,
							getLocaleDateFormat(defaultLanguageId),
							true
						).isValid()
					) {
						onChange(getValueForHidden(value));
					}
				}}
				ref={inputRef}
				spritemap={spritemap}
				value={value}

				// weekdaysShort={WeekdayShort}

				years={years}
			/>
		</>
	);
};

const Main = ({
	defaultLanguageId,
	locale = themeDisplay.getLanguageId(),
	localizedValue,
	id,
	name,
	onChange,
	placeholder,
	predefinedValue,
	readOnly,
	spritemap,
	value,
	...otherProps
}) => {
	const [geoDate, setGeoDate] = useState(null);
	const {enabled} = otherProps;

	const getDate = (dateString) => {
		if (!dateString) {
			return null;
		}

		const jalaliDate = jalali(dateString, 'YYYY-MM-DD').locale('fa');

		const result = {
			day: parseInt(jalaliDate.format('DD'), 10),
			month: parseInt(jalaliDate.format('MM'), 10),
			year: parseInt(jalaliDate.format('YYYY'), 10),
		};

		return result;
	};

	const initialDate = getDate(value);

	const [selectedDay, setSelectedDay] = useState(initialDate);

	const getSingleDayValue = (value) => {
		if (!value) {
			return '';
		}
		const year = value.year;
		const month = value.month;
		const day = value.day;

		return `${year}/${month}/${day}`;
	};

	const renderCustomInput = ({ref}) => (
		<ClayInput
			aria-labelledby={id}
			className="ddm-field-text"
			disabled={!enabled}
			name={name}
			ref={ref}
			type="text"
			value={selectedDay ? getSingleDayValue(selectedDay) : ''} // a styling class
		/>
	);

	if (locale == 'fa_IR') {
		return (
			<FieldBase
				{...otherProps}
				localizedValue={localizedValue}
				name={name}
				readOnly={readOnly}
				spritemap={spritemap}
			>
				<input
					aria-hidden="true"
					id={name + '_fieldDetails'}
					name={name}
					type="hidden"
					value={getValueForHidden(geoDate, locale)}
				/>

				<DatePicker
					inputPlaceholder="Select a day"
					locale="fa"
					onChange={(value) => {
						const geoDate = jalali
							.from(
								`${value.year}/${value.month}/${value.day}`,
								'fa',
								'YYYY/M/D'
							)
							.locale('en');
						setGeoDate(geoDate);
						setSelectedDay(value);
						onChange({}, geoDate);
					}}
					renderInput={renderCustomInput}
					shouldHighlightWeekends
					value={selectedDay}
					wrapperClassName="form-group"
				/>
			</FieldBase>
		);
	}
	else {
		return (
			<FieldBase
				{...otherProps}
				localizedValue={localizedValue}
				name={name}
				readOnly={readOnly}
				spritemap={spritemap}
			>
				<LiferayDatePicker
					defaultLanguageId={defaultLanguageId}
					disabled={readOnly}
					formatInEditingLocale={
						localizedValue && localizedValue[locale] != undefined
					}
					locale={locale}
					localizedValue={localizedValue}
					name={name}
					onChange={(value) => onChange({}, value)}
					placeholder={placeholder}
					spritemap={spritemap}
					value={
						value
							? getValueForHidden(value, locale)
							: predefinedValue
					}
				/>
			</FieldBase>
		);
	}
};

Main.displayName = 'DatePicker';

export default Main;
