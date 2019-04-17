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
import es.redmic.models.es.common.dto.MetaDataDTO;
import es.redmic.models.es.geojson.wrapper.GeoHitWrapper;
import es.redmic.viewlib.geodata.dto.GeoMetaDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class FeatureMapper extends CustomMapper<GeoHitWrapper, GeoMetaDTO> {

	@Override
	public void mapAtoB(GeoHitWrapper a, GeoMetaDTO b, MappingContext context) {

		Class<?> targetTypeDto = (Class<?>) context.getProperty("targetTypeDto");

		if (targetTypeDto == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);

		MetaDataDTO _meta = new MetaDataDTO();

		_meta.setScore(a.get_score());
		_meta.setVersion(a.get_version());
		_meta.setHighlight(a.getHighlight());

		b.set_meta(_meta);
	}
}
