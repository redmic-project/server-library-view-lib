package es.redmic.viewlib.common.mapper.es2dto;

import org.springframework.stereotype.Component;

import es.redmic.models.es.common.dto.JSONCollectionDTO;
import es.redmic.models.es.data.common.model.DataHitsWrapper;
import es.redmic.viewlib.data.dto.MetaDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class DataCollectionESMapper extends CustomMapper<DataHitsWrapper, JSONCollectionDTO> {

	@SuppressWarnings("unchecked")
	@Override
	public void mapAtoB(DataHitsWrapper a, JSONCollectionDTO b, MappingContext context) {
		b.setData(mapperFacade.mapAsList(a.getHits(), MetaDTO.class, context));
		b.get_meta().setMax_score(a.getMax_score());
		b.setTotal(a.getTotal());
	}
}