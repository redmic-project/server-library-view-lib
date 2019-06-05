package es.redmic.viewlib.common.mapper.es2dto;

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

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

import es.redmic.brokerlib.avro.geodata.common.FeatureDTO;
import es.redmic.brokerlib.avro.geodata.common.PropertiesBaseDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.geojson.wrapper.GeoHitWrapper;
import es.redmic.viewlib.geodata.dto.GeoMetaDTO;

public abstract class FeatureESMapper<TDTO extends FeatureDTO<PropertiesBaseDTO, Geometry>, TModel extends BaseES<?>>
		extends BaseESMapper<TDTO, TModel> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<GeoMetaDTO<TDTO>> mapList(List<GeoHitWrapper> geoHitWrapper) {

		List<GeoMetaDTO<TDTO>> list = new ArrayList<GeoMetaDTO<TDTO>>();
		for (GeoHitWrapper<TModel> entity : geoHitWrapper) {
			list.add(map(entity));
		}
		return list;
	}

	public GeoMetaDTO<TDTO> map(GeoHitWrapper<TModel> geoHitWrapper) {

		GeoMetaDTO<TDTO> result = new GeoMetaDTO<TDTO>();
		result.set_meta(getMetaDataDTO(geoHitWrapper));
		result.set_source(mapSource(geoHitWrapper.get_source()));
		return result;
	}
}
