package es.redmic.viewlib.common.mapper.es2dto;

import java.util.HashMap;
import java.util.Map;

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

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.models.es.common.dto.AggregationsDTO;
import es.redmic.models.es.common.dto.MetaDataDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.model.HitWrapper;
import es.redmic.models.es.common.model.SearchWrapper;

public abstract class BaseESMapper<TDTO extends CommonDTO, TModel extends BaseES<?>> {

	protected abstract TDTO mapSource(TModel model);

	protected MetaDataDTO getMetaDataDTO(HitWrapper hit) {

		MetaDataDTO _meta = new MetaDataDTO();
		_meta.setScore(hit.get_score());
		_meta.setVersion(hit.get_version());
		_meta.setHighlight(hit.getHighlight());
		return _meta;
	}

	AggregationsDTO getAggs(SearchWrapper wrapper) {

		AggregationsDTO aggs = new AggregationsDTO();

		if (wrapper.getAggregations() == null || wrapper.getAggregations().getAttributes().isEmpty())
			return aggs;

		Map<String, Object> attrs = new HashMap<>();

		wrapper.getAggregations().getAttributes().keySet().stream()
				.forEach(key -> attrs.put(getAggField(key), wrapper.getAggregations().getAttributes().get(key)));

		aggs.setAttributes(attrs);

		return aggs;
	}

	private String getAggField(String key) {
		String[] keySplitted = key.split("#");
		return keySplitted.length == 2 ? keySplitted[1] : key;
	}
}
