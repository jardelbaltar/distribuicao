package br.jus.distribuicao;

import java.util.*;

public class Magistrado implements Comparable<Magistrado> {
	private Integer id;
	private String nome;
	private Integer debito;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getDebito() {
		return debito;
	}

	public void setDebito(Integer debito) {
		this.debito = debito;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Magistrado))
			return false;
		Magistrado other = (Magistrado) obj;
		return id == null ? false : id.equals(other.id);
	}

	public int compareTo(Magistrado compareMagistrado) {

		int compareDebito = ((Magistrado) compareMagistrado).getDebito();

		// ascending order
		// return this.debito - compareDebito;

		// descending order
		return compareDebito - this.debito;

	}

	public static Comparator<Magistrado> MagistradoDebitoComparator = new Comparator<Magistrado>() {

		public int compare(Magistrado magistrado1, Magistrado magistrado2) {

			Integer debito1 = magistrado1.getDebito();
			Integer debito2 = magistrado2.getDebito();

			// ascending order
			// return debito1.compareTo(debito2);

			// descending order
			return debito2.compareTo(debito1);
		}

	};
}
