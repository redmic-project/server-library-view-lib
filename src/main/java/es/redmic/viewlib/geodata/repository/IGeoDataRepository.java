package es.redmic.viewlib.geodata.repository;

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

import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.geojson.common.model.Feature;
import es.redmic.models.es.geojson.wrapper.GeoHitWrapper;
import es.redmic.models.es.geojson.wrapper.GeoHitsWrapper;
import es.redmic.models.es.geojson.wrapper.GeoSearchWrapper;
import es.redmic.viewlib.common.repository.IBaseRepository;

public interface IGeoDataRepository<TModel extends Feature<?, ?>, TQueryDTO extends SimpleQueryDTO>
		extends IBaseRepository<TModel> {

	// R

	public GeoHitWrapper<?> findById(String id);

	public List<String> suggest(TQueryDTO queryDTO);

	public GeoHitsWrapper<?> mget(MgetDTO dto);

	public GeoSearchWrapper<?> find(TQueryDTO queryDTO);
}
