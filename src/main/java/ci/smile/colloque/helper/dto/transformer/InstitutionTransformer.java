package ci.smile.colloque.helper.dto.transformer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


import ci.smile.colloque.dao.entity.Institution;
import ci.smile.colloque.helper.dto.InstitutionDto;

@Mapper
public interface InstitutionTransformer {

	InstitutionTransformer INSTANCE = Mappers.getMapper( InstitutionTransformer.class ); 
	
	//-- Entity vers Dto
	
	InstitutionDto toDto(Institution institution); 
	List<InstitutionDto> toDtos(List<Institution> institution);
	
	//-- Dto vers Entity
	
	Institution toEntity( InstitutionDto institutionDto);
}
