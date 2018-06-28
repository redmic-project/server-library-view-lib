package es.redmic.viewlib.common.querymanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import es.redmic.models.es.common.view.QueryDTODeserializerModifier;
import es.redmic.restlib.common.service.UserUtilsServiceItfc;

@Component
public class QueryManagement {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UserUtilsServiceItfc userService;

	@Bean
	public SimpleModule queryDTOJsonViewModule() {

		return new SimpleModule().setDeserializerModifier(new QueryDTODeserializerModifier());
	}

	@Bean
	public QueryDTOMessageConverter queryDTOMessageConverter() {

		FilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false).addFilter("DataQueryDTO",
				SimpleBeanPropertyFilter.serializeAll());
		objectMapper.setFilterProvider(filters);

		return new QueryDTOMessageConverter(objectMapper, userService);
	}
}
