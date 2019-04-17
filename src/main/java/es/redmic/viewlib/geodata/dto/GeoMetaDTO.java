package es.redmic.viewlib.geodata.dto;

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

import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaIgnore;

import es.redmic.brokerlib.avro.geodata.common.FeatureDTO;
import es.redmic.brokerlib.avro.geodata.common.PropertiesBaseDTO;
import es.redmic.models.es.common.dto.MetaDataDTO;

public class GeoMetaDTO<TDTO extends FeatureDTO<PropertiesBaseDTO, Geometry>> {

	@JsonSchemaIgnore
	private MetaDataDTO _meta = new MetaDataDTO();

	@JsonUnwrapped
	private TDTO _source;

	public MetaDataDTO get_meta() {
		return _meta;
	}

	public void set_meta(MetaDataDTO _meta) {
		this._meta = _meta;
	}

	public TDTO get_source() {
		return _source;
	}

	public void set_source(TDTO _source) {
		this._source = _source;
	}
}
