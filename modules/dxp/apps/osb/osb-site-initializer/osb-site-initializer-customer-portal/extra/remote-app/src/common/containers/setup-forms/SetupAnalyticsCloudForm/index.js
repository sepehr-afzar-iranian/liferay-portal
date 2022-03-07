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

import ClayForm from '@clayui/form';
import {Formik} from 'formik';
import {isLowercaseAndNumbers} from '../../../../common/utils/validations.form';
import {Button, Input} from '../../../components';

// import getInitialDXPAdmin from '../../../utils/getInitialDXPAdmin';

import Layout from '../Layout';

const SetupAnalyticsCloudPage = ({handlePage, leftButton}) => {
	return (
		<Layout
			className="pt-1 px-3"
			footerProps={{
				leftButton: (
					<Button borderless onClick={() => handlePage()}>
						{leftButton}
					</Button>
				),
				middleButton: <Button displayType="primary">Submit</Button>,
			}}
			headerProps={{
				helper:
					'We’ll need a few details to finish creating your Analytics Cloud Workspace',
				title: 'Set up Analytics Cloud',
			}}
		>
			<ClayForm.Group className="mb-0">
				<ClayForm.Group className="mb-0 pb-1">
					<Input
						groupStyle="pb-1"
						helper="This user will create and manage the Analytics Cloud Workspace and must have a liferay.com account. The owner Email can be updated vis Support ticket if needed."
						label="Owner Email"
						name="dxp.projectId"
						placeholder="user@company.com"
						required
						type="email"
						validations={[(value) => isLowercaseAndNumbers(value)]}
					/>

					<Input
						groupStyle="pb-1"
						helper="Lowercase letters and numbers only. Project IDs cannot be changed."
						label="Workspace Name"
						name="dxp.projectId"
						placeholder="superbank1"
						required
						type="text"
					/>

					{/* <Select
						groupStyle="mb-0"
						helper="Select a server location for your data to be stored."
						label="Data Center Location"
						required
					/> */}

					<Input
						groupStyle="pb-1"
						helper="Please note that the friendly URL cannot be changed once added."
						label="Workspace Friendly URL"
						name="analytics.workspaceURL"
						placeholder="/myurl"
						type="text"
					/>
				</ClayForm.Group>
			</ClayForm.Group>

			<Button
				borderless
				className="ml-3 my-2 text-brand-primary"
				prependIcon="plus"
				small
			>
				Add Another Admin
			</Button>
		</Layout>
	);
};

const SetupAnalyticsCloudForm = (props) => {
	return (
		<Formik
			initialValues={{
				dxp: {
					// admins: [getInitialDXPAdmin()],

					dataCenterRegion: '',
					disasterDataCenterRegion: '',
					projectId: '',
				},
			}}
			validateOnChange
		>
			{(formikProps) => (
				<SetupAnalyticsCloudPage {...props} {...formikProps} />
			)}
		</Formik>
	);
};

export default SetupAnalyticsCloudForm;
