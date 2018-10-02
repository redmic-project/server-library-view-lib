package es.redmic.viewlib.common.mapper.es2dto;

import org.springframework.stereotype.Component;

import es.redmic.models.es.common.dto.MetaDataDTO;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.viewlib.data.dto.MetaDTO;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@SuppressWarnings("rawtypes")
@Component
public class DataItemESMapper extends CustomMapper<DataHitWrapper, MetaDTO> {

	@Override
	public void mapAtoB(DataHitWrapper a, MetaDTO b, MappingContext context) {

		MetaDataDTO _meta = new MetaDataDTO();

		_meta.setScore(a.get_score());
		_meta.setVersion(a.get_version());
		_meta.setHighlight(a.getHighlight());

		b.set_meta(_meta);
	}
}