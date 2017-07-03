package api;

import org.json.JSONArray;

class ReponseAPI {
	
	private boolean erreur;
	private String messageErreur;
	private JSONArray donnees;
	
	public ReponseAPI(JSONArray donnees, boolean err, String message) {
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
	
	public JSONArray getDonnees(){
		return this.donnees;
	}
	
}
