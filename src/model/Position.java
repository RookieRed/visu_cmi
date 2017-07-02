package model;

public class Position implements IPosition {
	
	private String batiment,
		salle,
		positionRelative;
	private int etage;
	
	public Position(String batiment, int etage, String salle, String positionRelative) {
		this.batiment= batiment;
		this.etage = etage;
		this.salle= salle;
		this.positionRelative= positionRelative;
	}

	public String getBatiment() {
		return batiment;
	}

	public String getSalle() {
		return salle;
	}

	public String getPositionRelative() {
		return positionRelative;
	}

	public int getEtage() {
		return etage;
	}

	@Override
	public String toString() {
		return "Batiment: " + batiment + " " + etage + "e etage, salle : " + salle 
				+ ", position relative : " + positionRelative;
	}
	
	
}
