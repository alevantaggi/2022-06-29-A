package it.polito.tdp.itunes.model;

import java.util.Objects;

public class BilancioAlbum implements Comparable<BilancioAlbum>{
	private Album a;
	private Integer bilancio;
	
	public BilancioAlbum(Album a, Integer bilancio) {
		this.a = a;
		this.bilancio = bilancio;
	}

	public Album getA() {
		return a;
	}

	public Integer getBilancio() {
		return bilancio;
	}

	@Override
	public int hashCode() {
		return Objects.hash(a, bilancio);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BilancioAlbum other = (BilancioAlbum) obj;
		return Objects.equals(a, other.a) && Objects.equals(bilancio, other.bilancio);
	}

	@Override
	public int compareTo(BilancioAlbum altro) {
		return -(this.bilancio-altro.getBilancio());
	}

	@Override
	public String toString() {
		return "" + a + ", bilancio=" + bilancio;
	}
	
	
	
}
