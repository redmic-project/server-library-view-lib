package es.redmic.viewlib.common.querymanagement;

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

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.models.es.common.query.dto.GeoDataQueryDTO;
import es.redmic.models.es.common.query.dto.MetadataQueryDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.models.es.common.view.JsonViewsForQueryDTO;
import es.redmic.restlib.common.service.UserUtilsServiceItfc;

public class QueryDTOMessageConverter extends MappingJackson2HttpMessageConverter {

	UserUtilsServiceItfc userService;

	public QueryDTOMessageConverter() {
		super();
	}

	public QueryDTOMessageConverter(ObjectMapper objectMapper, UserUtilsServiceItfc userService) {
		super(objectMapper);
		this.userService = userService;
	}

	@Override
	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		if (checkIsNotQueryDTO(type)) {
			return super.read(type, contextClass, inputMessage);
		}

		objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

		MappingJacksonInputMessage mess = new MappingJacksonInputMessage(inputMessage.getBody(),
				inputMessage.getHeaders());

		Class<?> currentView = JsonViewsForQueryDTO.getJsonView(userService.getUserRole());
		mess.setDeserializationView(currentView);

		return super.read(type, contextClass, mess);
	}

	private boolean checkIsNotQueryDTO(Type type) {

		return !(type.getTypeName().equals("TQueryDTO") || type.equals(DataQueryDTO.class)
				|| type.equals(GeoDataQueryDTO.class) || type.equals(MetadataQueryDTO.class)
				|| type.equals(SimpleQueryDTO.class));
	}
}
