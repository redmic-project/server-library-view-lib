package es.redmic.viewlib.common.service;

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

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import es.redmic.brokerlib.avro.common.CommonDTO;
import es.redmic.models.es.common.DataPrefixType;
import es.redmic.models.es.common.model.BaseES;
import es.redmic.models.es.common.query.dto.FilterSchemaDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.common.view.JsonViewsForQueryDTO;
import es.redmic.models.es.common.view.JsonViewsForQueryDTO.ViewClassInterface;
import es.redmic.models.es.utils.JacksonFieldUtils;
import es.redmic.restlib.config.UserService;

public abstract class RBaseService<TModel extends BaseES<?>, TDTO extends CommonDTO, TQueryDTO extends SimpleQueryDTO> {

	protected Class<TDTO> typeOfTDTO;

	protected Class<TModel> typeOfTModel;

	protected Class<TQueryDTO> typeOfTQueryDTO;

	protected Map<Object, Object> globalProperties = new HashMap<Object, Object>();

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	UserService userService;

	@SuppressWarnings("unchecked")
	public RBaseService() {

		this.typeOfTModel = (Class<TModel>) (((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]);

		this.typeOfTDTO = (Class<TDTO>) (((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[1]);

		this.typeOfTQueryDTO = (Class<TQueryDTO>) (((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[2]);
	}

	@SuppressWarnings("unchecked")
	public FilterSchemaDTO getFilterSchema(Set<String> ignorableFieldNames) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JodaModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Class<?> jsonView = getJsonView();

		Set<String> fieldsToFilter = new HashSet<String>();
		Set<String> fieldsNoIncludedInView = JacksonFieldUtils.getFieldNamesNoIncludedInView(typeOfTQueryDTO,
				(Class<ViewClassInterface>) jsonView);

		if (fieldsNoIncludedInView != null && fieldsNoIncludedInView.size() > 0)
			fieldsToFilter.addAll(fieldsNoIncludedInView);

		if (ignorableFieldNames != null)
			fieldsToFilter.addAll(ignorableFieldNames);

		FilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false).addFilter("DataQueryDTO",
				SimpleBeanPropertyFilter.serializeAllExcept(fieldsToFilter));
		mapper.setFilterProvider(filters);

		JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(mapper);
		JsonNode jsonSchema = jsonSchemaGenerator.generateJsonSchema(typeOfTQueryDTO);

		FilterSchemaDTO dto = new FilterSchemaDTO();
		dto.setSchema(objectMapper.convertValue(jsonSchema, Map.class));

		return dto;
	}

	public Class<?> getJsonView() {
		return JsonViewsForQueryDTO.getJsonView(userService.getUserRole());
	}

	protected void processQuery(TQueryDTO queryDTO, Map<String, Object> fixedQuery, Set<String> fieldsExcludedOnQuery) {

		queryDTO.addAccessibilityIds(userService.getAccessibilityControl());

		if (fixedQuery != null && fixedQuery.size() > 0) {
			queryDTO.setFixedQuery(fixedQuery);
		}

		if (fieldsExcludedOnQuery != null && fieldsExcludedOnQuery.size() > 0) {
			queryDTO.setFieldsExcludedOnQuery(fieldsExcludedOnQuery);
		}

		queryDTO.setDataType(DataPrefixType.getPrefixTypeFromClass(typeOfTDTO));

		queryDTO.checkFieldsExcludedOnQuery();
	}

	protected abstract String[] getDefaultSearchFields();

	protected abstract String[] getDefaultHighlightFields();

	protected abstract String[] getDefaultSuggestFields();
}
