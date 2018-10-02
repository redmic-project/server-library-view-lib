package es.redmic.viewlib.geodata.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kjetland.jackson.jsonSchema.annotations.JsonSchemaIgnore;
import com.vividsolutions.jts.geom.Geometry;

import es.redmic.brokerlib.avro.geodata.common.FeatureDTO;
import es.redmic.brokerlib.avro.geodata.common.PropertiesBaseDTO;
import es.redmic.models.es.common.dto.MetaDataDTO;

public class GeoMetaDTO<TDTO extends FeatureDTO<PropertiesBaseDTO, Geometry>> {

	@JsonSchemaIgnore
	private MetaDataDTO _meta = new MetaDataDTO();

	@JsonUnwrapped
	private TDTO _source;

	public MetaDataDTO get_meta() {
		return _meta;
	}

	public void set_meta(MetaDataDTO _meta) {
		this._meta = _meta;
	}

	public TDTO get_source() {
		return _source;
	}

	public void set_source(TDTO _source) {
		this._source = _source;
	}
}
