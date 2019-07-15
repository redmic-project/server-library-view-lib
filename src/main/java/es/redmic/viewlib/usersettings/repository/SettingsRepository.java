package es.redmic.viewlib.usersettings.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

/*-
 * #%L
 * view-lib
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.usersettingslib.model.Settings;
import es.redmic.usersettingslib.repository.SettingsRepositoryImpl;
import es.redmic.viewlib.data.repository.IDataRepository;

@Repository
@ConditionalOnProperty(name = "redmic.user-settings.enabled", havingValue = "true")
public class SettingsRepository extends SettingsRepositoryImpl<Settings, SimpleQueryDTO>
		implements IDataRepository<Settings, SimpleQueryDTO> {

	public SettingsRepository() {
		super();
	}
}
