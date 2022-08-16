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

import {render} from 'frontend-js-react-web';
import jalali from 'jalali-moment';
import React, {useState} from 'react';
import DatePicker from 'react-modern-calendar-datepicker';

import 'react-modern-calendar-datepicker/lib/DatePicker.css';

import './DatePicker.scss';

const Wrapper = ({
	dayParamId,
	disabled,
	inputId,
	local,
	monthParamId,
	value,
	yearParamId,
}) => {
	const getDate = (dateString) => {
		if (!dateString) {
			return null;
		}
		const dateArray = dateString.split('/');

		if (local != 'fa') {
			return {
				day: parseInt(dateArray[1], 10),
				month: parseInt(dateArray[0], 10),
				year: parseInt(dateArray[2], 10),
			};
		}
		else {
			const jalaliDate = jalali(dateString, 'MM/DD/YYYY').locale('fa');

			const result = {
				day: parseInt(jalaliDate.format('DD'), 10),
				month: parseInt(jalaliDate.format('MM'), 10),
				year: parseInt(jalaliDate.format('YYYY'), 10),
			};

			return result;
		}
	};

	const getSingleDayValue = (value) => {
		if (!value) {
			return '';
		}
		const year = value.year;
		const month = value.month;
		const day = value.day;

		return `${year}/${month}/${day}`;
	};

	const initialDate = getDate(value);

	const [selectedDay, setSelectedDay] = useState(initialDate);

	const renderCustomInput = ({ref}) => (
		<input
			className="field form-control lfr-input-text"
			disabled={disabled}
			id={inputId} // necessary
			placeholder="yyyy/mm/dd"
			readOnly
			ref={ref}
			value={selectedDay ? getSingleDayValue(selectedDay) : ''} // a styling class
		/>
	);

	return (
		<DatePicker
			calendarPopperPosition="top"
			inputClassName="field form-control lfr-input-text"
			locale={local}
			onChange={(value) => {
				const geoDate = jalali
					.from(
						`${value.year}/${value.month}/${value.day}`,
						local,
						'YYYY/M/D'
					)
					.locale('en');

				// Because of java Month start from zero and end with 11

				const month = parseInt(geoDate.format('MM'), 10) - 1;

				document.getElementById(yearParamId).value = geoDate.format(
					'YYYY'
				);

				document.getElementById(monthParamId).value = month;

				document.getElementById(dayParamId).value = geoDate.format(
					'DD'
				);

				setSelectedDay(value);
			}} // render a custom input
			renderInput={renderCustomInput}
			shouldHighlightWeekends
			value={selectedDay}
			wrapperClassName={disabled ? 'disable' : ''}
		/>
	);
};

function datePicker(id, props) {
	render(Wrapper, props, document.getElementById(id));
}

export {datePicker};
export default datePicker;
