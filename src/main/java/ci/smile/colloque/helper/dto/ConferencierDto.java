/*
 * Created on 18 oct. 2018 ( Time 18:53:49 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
// This Bean has a basic Primary Key (not composite) 

package ci.smile.colloque.helper.dto;

import ci.smile.colloque.helper.contract.SearchParam;

/**
 * Persistent class for entity stored in table "conferencier"
 *
 * @author Telosys Tools Generator
 *
 */


public class ConferencierDto implements Cloneable {


    private Integer participantId ;
    private Boolean    organisateur;
    private Integer institutionId ;
    
    private InscriptionDto inscriptionDto ;
    private String institutionName;
    private ExposerDto exposer;
    
        
    
    private SearchParam<Integer> participantIdParam ;
    private SearchParam<Boolean>    organisateurParam;
    private SearchParam<Integer> institutionIdParam ;

    
    //----------------------------------------------------------------------
    // GETTER & SETTER FOR THE KEY FIELD
    //----------------------------------------------------------------------

    public void setParticipantId( Integer participantId ) {
        this.participantId = participantId ;
    }
    public Integer getParticipantId() {
        return this.participantId;
    }


 


    public void setInstitutionId( Integer institutionId ) {
        this.institutionId = institutionId;
    }
    public Integer getInstitutionId() {
        return this.institutionId;
    }



    public Boolean getOrganisateur() {
		return this.organisateur ;
	}
    
	public void setOrganisateur(Boolean organisateur) {
		this.organisateur = organisateur;
	}
	//----------------------------------------------------------------------
    // clone METHOD
    //----------------------------------------------------------------------
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	public SearchParam<Integer> getParticipantIdParam() {
		return participantIdParam;
	}
	public void setParticipantIdParam(SearchParam<Integer> participantIdParam) {
		this.participantIdParam = participantIdParam;
	}
	public SearchParam<Boolean> getOrganisateurParam() {
		return organisateurParam;
	}
	public void setOrganisateurParam(SearchParam<Boolean> organisateurParam) {
		this.organisateurParam = organisateurParam;
	}
	public SearchParam<Integer> getInstitutionIdParam() {
		return institutionIdParam;
	}
	public void setInstitutionIdParam(SearchParam<Integer> institutionIdParam) {
		this.institutionIdParam = institutionIdParam;
	}

	public String getInstitutionName() {
		return institutionName;
	}
	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}
	public InscriptionDto getInscriptionDto() {
		return inscriptionDto;
	}
	public void setInscriptionDto(InscriptionDto inscriptionDto) {
		this.inscriptionDto = inscriptionDto;
	}
	public ExposerDto getExposer() {
		return exposer;
	}
	public void setExposer(ExposerDto exposer) {
		this.exposer = exposer;
	}

}