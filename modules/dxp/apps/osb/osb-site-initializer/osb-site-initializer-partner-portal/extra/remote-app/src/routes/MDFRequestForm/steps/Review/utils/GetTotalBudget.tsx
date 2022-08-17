/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import {useMemo} from 'react';

import MDFRequest from '../../../../../common/interfaces/mdfRequest';
import MDFRequestActivity from '../../../../../common/interfaces/mdfRequestActivity';

const GetTotalBudget = (values: MDFRequest) => {
	const totalBudget = useMemo(
		() =>
			values.activities.reduce(
				(previousValue: number, currentValue: MDFRequestActivity) => {
					const sumBudgets = currentValue.budgets.reduce<number>(
						(previousValue, currentValue) =>
							previousValue + +currentValue.cost,
						0
					);

					return previousValue + sumBudgets;
				},
				0
			),
		[values.activities]
	);

	return totalBudget;
};
export default GetTotalBudget;
