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

import java.util.List;

import es.redmic.brokerlib.avro.geodata.common.FeatureDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.wrapper.GeoHitWrapper;
import es.redmic.models.es.geojson.wrapper.GeoHitsWrapper;
import es.redmic.models.es.geojson.wrapper.GeoSearchWrapper;
import es.redmic.viewlib.common.service.RBaseService;
import es.redmic.viewlib.geodata.dto.GeoMetaDTO;
import es.redmic.viewlib.geodata.repository.IGeoDataRepository;

public abstract class RGeoDataService<TModel extends Feature<?, ?>, TDTO extends FeatureDTO<?, ?>, TQueryDTO extends SimpleQueryDTO>
		extends RBaseService<TModel, TDTO, TQueryDTO> implements IGeoDataService<TModel, TDTO, TQueryDTO> {

	IGeoDataRepository<TModel, TQueryDTO> repository;

	public RGeoDataService(IGeoDataRepository<TModel, TQueryDTO> repository) {
		this.repository = repository;
	}

	@Override
	public GeoMetaDTO<?> findById(String id, String parentId) {

		// TODO: comprobar mediante microservicio de credenciales que este usuario puede
		// buscar

		return viewResultToDTO(repository.findById(id));
	}

	@Override
	public List<String> suggest(TQueryDTO queryDTO, String parentId) {

		// TODO: comprobar mediante microservicio de credenciales que este usuario puede
		// buscar

		return repository.suggest(queryDTO);
	}

	@Override
	public GeoJSONFeatureCollectionDTO mget(MgetDTO dto, String parentId) {

		// TODO: comprobar mediante microservicio de credenciales que este usuario puede
		// buscar

		return viewResultToDTO(repository.mget(dto));
	}

	@Override
	public GeoJSONFeatureCollectionDTO find(TQueryDTO query, String parentId) {

		// TODO: comprobar mediante microservicio de credenciales que este usuario puede
		// buscar

		return viewResultToDTO(repository.find(query));
	}

	protected abstract GeoMetaDTO<?> viewResultToDTO(GeoHitWrapper<?> geoHitWrapper);

	protected abstract GeoJSONFeatureCollectionDTO viewResultToDTO(GeoSearchWrapper<?> viewResult);

	protected abstract GeoJSONFeatureCollectionDTO viewResultToDTO(GeoHitsWrapper<?> viewResult);
}
