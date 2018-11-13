package ci.smile.colloque.helper.dto.transformer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


import ci.smile.colloque.dao.entity.Universite;
import ci.smile.colloque.helper.dto.UniversiteDto;

@Mapper
public interface UniversiteTransformer {

	UniversiteTransformer INSTANCE = Mappers.getMapper(UniversiteTransformer.class);
	
	
	//from entity to dto
	
	UniversiteDto todto(Universite universite);
	List<UniversiteDto> todtos(List<Universite> universite);
	
	// from dto to entity
	
	Universite toEntity(UniversiteDto universiteDto);
	
}
