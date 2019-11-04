package es.redmic.viewlib.common.mapper.es2dto;

import es.redmic.brokerlib.avro.common.CommonDTO;

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

import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import es.redmic.models.es.data.common.model.DataSearchWrapper;

public abstract class DataCollectionESMapper<TDTO extends CommonDTO, TModel extends BaseES<?>>
		extends DataItemESMapper<TDTO, TModel> {

	@SuppressWarnings({ "rawtypes" })
	public JSONCollectionDTO map(DataSearchWrapper dataSearchWrapper) {

		JSONCollectionDTO result = map(dataSearchWrapper.getHits());
		result.set_aggs(getAggs(dataSearchWrapper));
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JSONCollectionDTO map(DataHitsWrapper dataHitsWrapper) {

		JSONCollectionDTO result = new JSONCollectionDTO();
		result.setData(mapList(dataHitsWrapper.getHits()));
		result.get_meta().setMax_score(dataHitsWrapper.getMax_score());
		result.setTotal(dataHitsWrapper.getTotal());
		return result;
	}
}
