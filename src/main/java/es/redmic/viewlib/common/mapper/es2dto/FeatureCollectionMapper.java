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

import org.springframework.stereotype.Component;

import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.wrapper.GeoHitsWrapper;
import es.redmic.viewlib.geodata.dto.GeoMetaDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class FeatureCollectionMapper extends CustomMapper<GeoHitsWrapper, GeoJSONFeatureCollectionDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public void mapAtoB(GeoHitsWrapper a, GeoJSONFeatureCollectionDTO b, MappingContext context) {

		Class<?> targetTypeDto = (Class<?>) context.getProperty("targetTypeDto");

		if (targetTypeDto == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);

		b.setFeatures(mapperFacade.mapAsList(a.getHits(), GeoMetaDTO.class, context));
		b.get_meta().setMax_score(a.getMax_score());
		b.setTotal(a.getTotal());
	}
}
