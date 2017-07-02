package api;

import org.json.JSONObject;

class ReponseAPI {
	
	private boolean erreur;
	private String messageErreur;
	private JSONObject donnees;
	
	public ReponseAPI(JSONObject donnees, boolean err, String message) {
		this.erreur = err;
		this.donnees = donnees;
		this.messageErreur = message;
	}
	
	public boolean erreur(){
		return this.donnees == null || this.erreur;
	}
	
	public String getMessageErreur(){
		return this.messageErreur;
	}
	
	public JSONObject getDonnees(){
		return this.donnees;
	}
	
}
