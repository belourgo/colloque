package ci.smile.colloque.helper.dto.transformer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import ci.smile.colloque.dao.entity.Colloque;
import ci.smile.colloque.dao.entity.Universite;
import ci.smile.colloque.helper.dto.ColloqueDto;

@Mapper
public interface ColloqueTransformer {

	ColloqueTransformer INSTANCE = Mappers.getMapper( ColloqueTransformer.class );
	
	//-- Entity vers DTO
	
	
	
	@Mappings({
		@Mapping(source = "universite.id", target = "universiteId"),
		@Mapping(source = "date",dateFormat="dd/MM/yyyy", target = "date")
			})	
	ColloqueDto toDto(Colloque colloque );
	List<ColloqueDto> toDtos(List<Colloque> colloque);
	
	//-- DTO vers Entity
//	

	
	@Mappings({
		@Mapping(source = "colloqueDto.id", target = "id"),
		@Mapping(source = "colloqueDto.nom", target = "nom"),
		@Mapping(source = "universite", target = "universite"),
		@Mapping(source = "date",dateFormat="dd/MM/yyyy", target = "date")
			})	
	Colloque toEntity(ColloqueDto colloqueDto,Universite universite, String date);
	
}
