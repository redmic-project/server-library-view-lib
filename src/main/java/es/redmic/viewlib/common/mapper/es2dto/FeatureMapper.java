package es.redmic.viewlib.common.mapper.es2dto;

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