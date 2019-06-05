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

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.viewlib.data.dto.MetaDTO;

public abstract class DataItemESMapper<TDTO extends CommonDTO, TModel extends BaseES<?>>
		extends BaseESMapper<TDTO, TModel> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<MetaDTO<TDTO>> mapList(List<DataHitWrapper> dataHitWrapper) {

		List<MetaDTO<TDTO>> list = new ArrayList<MetaDTO<TDTO>>();
		for (DataHitWrapper<TModel> entity : dataHitWrapper) {
			list.add(map(entity));
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MetaDTO map(DataHitWrapper dataHitWrapper) {

		MetaDTO<TDTO> result = new MetaDTO<TDTO>();
		result.set_meta(getMetaDataDTO(dataHitWrapper));
		result.set_source(mapSource((TModel) dataHitWrapper.get_source()));
		return result;
	}
}
