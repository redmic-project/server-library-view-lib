package es.redmic.viewlib.usersettings.service;

import org.mapstruct.factory.Mappers;

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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;
import es.redmic.usersettingslib.dto.SettingsDTO;
import es.redmic.usersettingslib.model.Settings;
import es.redmic.viewlib.data.dto.MetaDTO;
import es.redmic.viewlib.data.service.RWDataService;
import es.redmic.viewlib.usersettings.mapper.SettingsESMapper;
import es.redmic.viewlib.usersettings.repository.SettingsRepository;

@Service
@ConditionalOnProperty(name = "redmic.user-settings.enabled", havingValue = "true")
public class SelectionService extends RWDataService<Settings, SettingsDTO, SimpleQueryDTO> {

	public SelectionService(SettingsRepository repository) {
		super(repository);
	}

	@Override
	protected MetaDTO<?> viewResultToDTO(DataHitWrapper<?> viewResult) {
		return Mappers.getMapper(SettingsESMapper.class).map(viewResult);
	}

	@Override
	protected JSONCollectionDTO viewResultToDTO(DataSearchWrapper<?> viewResult) {
		return Mappers.getMapper(SettingsESMapper.class).map(viewResult);
	}

	@Override
	protected JSONCollectionDTO viewResultToDTO(DataHitsWrapper<?> viewResult) {
		return Mappers.getMapper(SettingsESMapper.class).map(viewResult);
	}
}
