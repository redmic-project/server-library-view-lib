package es.redmic.viewlib.common.querymanagement;

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