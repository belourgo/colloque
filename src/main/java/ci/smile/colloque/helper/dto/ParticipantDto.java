/*
 * Created on 18 oct. 2018 ( Time 18:53:49 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
// This Bean has a basic Primary Key (not composite) 

package ci.smile.colloque.helper.dto;

import ci.smile.colloque.helper.contract.SearchParam;

/**
 * Persistent class for entity stored in table "participant"
 *
 * @author Telosys Tools Generator
 *
 */


public class ParticipantDto implements  Cloneable {

    private Integer    id           ;
    private String     nom          ;
    private String     prenom       ;
    private String     numeroDeParticipation ;
    private String     adresse      ;
    
    
    
    private SearchParam<Integer>    idParam           ;
    private SearchParam<String>     nomParam          ;
    private SearchParam<String>     prenomParam       ;
    private SearchParam<String>     numeroDeParticipationParam ;
    private SearchParam<String>     adresseParam      ;


    //----------------------------------------------------------------------
    // GETTER & SETTER FOR THE KEY FIELD
    //----------------------------------------------------------------------
    public void setId( Integer id ) {
        this.id = id ;
    }
    public Integer getId() {
        return this.id;
    }

 
    public void setNom( String nom ) {
        this.nom = nom;
    }
    public String getNom() {
        return this.nom;
    }

    public void setPrenom( String prenom ) {
        this.prenom = prenom;
    }
    public String getPrenom() {
        return this.prenom;
    }


    public void setNumeroDeParticipation( String numeroDeParticipation ) {
        this.numeroDeParticipation = numeroDeParticipation;
    }
    public String getNumeroDeParticipation() {
        return this.numeroDeParticipation;
    }


    public void setAdresse( String adresse ) {
        this.adresse = adresse;
    }
    public String getAdresse() {
        return this.adresse;
    }



	public SearchParam<Integer> getIdParam() {
		return idParam;
	}
	public void setIdParam(SearchParam<Integer> idParam) {
		this.idParam = idParam;
	}
	public SearchParam<String> getNomParam() {
		return nomParam;
	}
	public void setNomParam(SearchParam<String> nomParam) {
		this.nomParam = nomParam;
	}
	public SearchParam<String> getPrenomParam() {
		return prenomParam;
	}
	public void setPrenomParam(SearchParam<String> prenomParam) {
		this.prenomParam = prenomParam;
	}
	public SearchParam<String> getNumeroDeParticipationParam() {
		return numeroDeParticipationParam;
	}
	public void setNumeroDeParticipationParam(SearchParam<String> numeroDeParticipationParam) {
		this.numeroDeParticipationParam = numeroDeParticipationParam;
	}
	public SearchParam<String> getAdresseParam() {
		return adresseParam;
	}
	public void setAdresseParam(SearchParam<String> adresseParam) {
		this.adresseParam = adresseParam;
	}
	//----------------------------------------------------------------------
    // clone METHOD
    //----------------------------------------------------------------------
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
