package model;

public class Mesure {

	private TypeCapteur type;
	private float valeur;
	
	public Mesure(TypeCapteur type) {
		this.type = type;
		this.valeur = 0f;
	}
	
	public void setValeur(float valeur){
		this.valeur = valeur;
	}
	
	public float getValeur() {
		return valeur;
	}
	
	public String getType(){
		return this.type.getValue();
	}
	
	@Override
	public String toString() {
		return this.valeur + "";
	}
	
}
