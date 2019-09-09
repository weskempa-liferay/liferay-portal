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

import React, {useState} from 'react';
import EditAppFooter from './EditAppFooter.es';
import MultiStepNav from './MultiStepNav.es';
import ControlMenu from '../../components/control-menu/ControlMenu.es';
import {UpperToolbarInput} from '../../components/upper-toolbar/UpperToolbar.es';
import {addItem, updateItem} from '../../utils/client.es';

export default ({
	history,
	match: {
		params: {dataDefinitionId, appId}
	}
}) => {
	const [app, setApp] = useState({
		app: {
			name: {
				en_US: ''
			}
		}
	});

	const [currentStep, setCurrentStep] = useState(1);

	let title = Liferay.Language.get('new-app');

	if (appId) {
		title = Liferay.Language.get('edit-app');
	}

	const handleBack = () => {
		history.push(`/custom-object/${dataDefinitionId}/apps`);
	};

	const handleSubmit = () => {
		if (app.name.en_US === '') {
			return;
		}

		if (appId) {
			updateItem(`/o/app-builder/v1.0/apps/${appId}`, app).then(
				handleBack
			);
		} else {
			addItem(
				`/o/app-builder/v1.0/data-definitions/${dataDefinitionId}/apps`,
				app
			).then(handleBack);
		}
	};

	const handleAppNameChange = event => {
		setApp(prevState => ({
			...prevState.app,
			name: {
				en_US: event.target.value
			}
		}));
	};

	return (
		<>
			<ControlMenu backURL="../" title={title} />

			<div className="container-fluid container-fluid-max-lg mt-4">
				<div className="card card-root">
					<div className="card-header align-items-center d-flex justify-content-between bg-transparent">
						<UpperToolbarInput
							onInput={handleAppNameChange}
							placeholder={Liferay.Language.get('untitled-app')}
							value={app.en_US}
						/>
					</div>

					<h4 className="card-divider"></h4>

					<div className="card-body p-0">
						<div className="autofit-row">
							<div className="col-md-12">
								<MultiStepNav currentStep={currentStep} />
							</div>
						</div>

						{currentStep == 1 && (
							<div className="autofit-row">
								<div className="col-md-12">Hello1</div>
							</div>
						)}
						{currentStep == 2 && (
							<div className="autofit-row">
								<div className="col-md-12">Hello2</div>
							</div>
						)}
						{currentStep == 3 && (
							<div className="autofit-row">
								<div className="col-md-12">Hello3</div>
							</div>
						)}
					</div>

					<EditAppFooter
						currentStep={currentStep}
						onCancel={() => {}}
						onStepChange={step => setCurrentStep(step)}
					/>
				</div>
			</div>
		</>
	);
};
