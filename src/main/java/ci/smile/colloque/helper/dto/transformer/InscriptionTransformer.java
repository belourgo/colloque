package ci.smile.colloque.helper.dto.transformer;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import ci.smile.colloque.dao.entity.Colloque;
import ci.smile.colloque.dao.entity.Inscription;
import ci.smile.colloque.dao.entity.Participant;
import ci.smile.colloque.helper.dto.InscriptionDto;

@Mapper
public interface InscriptionTransformer {

	
	InscriptionTransformer INSTANCE = Mappers.getMapper( InscriptionTransformer.class ); 
	
	//-- Entity vers Dto
	
	//--- Multiple Mapping
		@Mappings({
			@Mapping(source = "participant.id", target = "participantId"),
			@Mapping(source = "colloque.id", target = "colloqueId"),
			@Mapping(source = "date",dateFormat="dd/MM/yyyy", target = "date")
				})
	
	InscriptionDto toDto(Inscription inscription); 
	List<InscriptionDto> toDtos(List<Inscription> inscription );
	
	//-- Dto vers Entity
	
	//--- Multiple Mapping
			@Mappings({

				//@Mapping(source = "inscriptionDto.date", target = "date"),
				@Mapping(source = "inscriptionDto.id", target = "id"),
				@Mapping(source = "participant", target = "participant"),
				@Mapping(source = "colloque", target = "colloque"),
				@Mapping(source = "date",dateFormat="dd/MM/yyyy", target = "date")
					})
	
	Inscription toEntity( InscriptionDto inscriptionDto, 
			Participant participant, Colloque colloque, String date);
}
