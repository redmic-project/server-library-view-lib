package es.redmic.viewlib.geodata.service;

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

import es.redmic.brokerlib.avro.geodata.common.FeatureDTO;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.geojson.base.Feature;
import es.redmic.viewlib.geodata.repository.IGeoDataRepository;

public abstract class RWGeoDataService<TModel extends Feature<?, ?>, TDTO extends FeatureDTO<?, ?>, TQueryDTO extends SimpleQueryDTO>
		extends RGeoDataService<TModel, TDTO, TQueryDTO> {

	IGeoDataRepository<TModel, TQueryDTO> repository;

	public RWGeoDataService(IGeoDataRepository<TModel, TQueryDTO> repository) {
		super(repository);
		this.repository = repository;
	}

	public EventApplicationResult save(TModel model) {

		return repository.save(model);
	}

	public EventApplicationResult update(TModel model) {

		return repository.update(model);
	}

	public EventApplicationResult delete(String id) {

		return repository.delete(id);
	}
}
