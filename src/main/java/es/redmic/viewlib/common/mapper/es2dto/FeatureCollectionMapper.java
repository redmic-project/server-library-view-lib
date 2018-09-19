package es.redmic.viewlib.common.mapper.es2dto;

import org.springframework.stereotype.Component;

import es.redmic.exception.common.ExceptionType;
import es.redmic.exception.common.InternalException;
import es.redmic.models.es.geojson.common.dto.GeoJSONFeatureCollectionDTO;
import es.redmic.models.es.geojson.wrapper.GeoHitsWrapper;
import es.redmic.viewlib.geodata.dto.GeoMetaDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class FeatureCollectionMapper extends CustomMapper<GeoHitsWrapper, GeoJSONFeatureCollectionDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public void mapAtoB(GeoHitsWrapper a, GeoJSONFeatureCollectionDTO b, MappingContext context) {

		Class<?> targetTypeDto = (Class<?>) context.getProperty("targetTypeDto");

		if (targetTypeDto == null)
			throw new InternalException(ExceptionType.INTERNAL_EXCEPTION);

		b.setFeatures(mapperFacade.mapAsList(a.getHits(), GeoMetaDTO.class, context));
		b.get_meta().setMax_score(a.getMax_score());
		b.setTotal(a.getTotal());
	}
}