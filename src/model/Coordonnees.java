package model;

public class Coordonnees implements IPosition {
	
	private float latitude, longitude;
	
	public Coordonnees(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}
	public float getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return "Coordonnees : latitude=" + latitude + ", longitude=" + longitude;
	}
	
}
