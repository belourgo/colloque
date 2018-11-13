package ci.smile.colloque.helper.dto.transformer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import ci.smile.colloque.dao.entity.Colloque;
import ci.smile.colloque.dao.entity.Organisateur;
import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.helper.dto.OrganisateurDto;

@Mapper
public interface OrganisateurTransformer {
	
	OrganisateurTransformer INSTANCE = Mappers.getMapper(OrganisateurTransformer.class);
	
	
	//from entity to dto
	
	//--- Multiple Mapping
			@Mappings({
				
				@Mapping(source = "participant.id", target = "participantId"),
				@Mapping(source = "colloque.id", target = "colloqueId")
					})
	OrganisateurDto toDto(Organisateur organisateur);
	List<OrganisateurDto> toDtos(List<Organisateur> organisateur);
	
	// from dto to entity
	
	//--- Multiple Mapping
	@Mappings({
		@Mapping(source = "organisateurDto.id", target = "id"),
		@Mapping(source = "participant", target = "participant"),
		@Mapping(source = "colloque", target = "colloque")
			})
	Organisateur toEntity(OrganisateurDto organisateurDto, Participant participant, Colloque colloque);
	
}
