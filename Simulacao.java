package br.jus.distribuicao;

import java.util.*;

public class Simulacao {
	public static HashMap<Integer, Integer> controleDiferencaAtual;
	public static HashMap<Integer, Integer> controleDoTotalDistribuido;
	public static HashMap<Integer, Integer> controleDoTotalDistribuidoDoDia;
	public static HashMap<Integer, Integer> controleDoTotalDeSorteados;
	public static List<Magistrado> lista = new ArrayList<Magistrado>();
	private static int totalDeMagistrados;
	private static int totalDeDistribuicoes;
	private static int limiteDeDistribuicoes;
	private static int totalDeProcessos;
	private static int grandeza;
	private static int distanciaAtual;
	private static int distanciaInicial;
	private static boolean considerarGrandeza;
	private static boolean simularQuantidadeDeProcessos;
	private static boolean subtrairQuantidadeDeProcessosRecebidos;
	private static boolean somarQuantidadeDeProcessosNaoRecebidos;
	private static int sementeDoDesequilibrio;

	public static void main(String[] args) {
		Simulacao.distribuir();
	}

	public static void distribuir() {
		try {
			sementeDoDesequilibrio = 3;
			totalDeMagistrados = 40;
			totalDeProcessos = 20;
			limiteDeDistribuicoes = 500;
			simularQuantidadeDeProcessos = false;
			considerarGrandeza = false;
			subtrairQuantidadeDeProcessosRecebidos = true;
			somarQuantidadeDeProcessosNaoRecebidos = false;
			totalDeDistribuicoes = 0;

			carregarControleDiferenca();
			System.out.println(
					"Tabela de Diferenças inicial: " + controleDiferencaAtual);

			inicializaListaComTabelaDeDebito();
			carregarControleDoTotalDistribuido();
			carregarControleDoTotalDeSorteados();
			calculaDistancia();
			distanciaInicial = distanciaAtual;
			System.out.println("Distância Inicial: " + distanciaAtual);
			for (int i = 1; i <= limiteDeDistribuicoes; i++) {
				if (distanciaAtual < 3) {
					System.out
							.println("Distância Inicial: " + distanciaInicial);
					throw new RuntimeException("SISTEMA EM EQUILÍBRIO!");
				}
				totalDeDistribuicoes = i;
				carregarControleDoTotalDistribuidoDoDia();
				Random random = new Random();
				if (simularQuantidadeDeProcessos) {
					// limite de processos a 80% do quantitativo de magistrados
					// quando o total de processos é simulado.
					// Sem este limite, pode acontecer de não conseguir sortear.

					totalDeProcessos = random.nextInt(
							(int) Math.ceil(totalDeMagistrados * 0.8)) + 1;
				}
				System.out.println(
						"-------------------------------------------------------------------");
				System.out.println("Distribuição " + i);
				System.out.println("Total de processos:" + totalDeProcessos);
				atualizaListaComTabelaDeDebito();
				calcularGrandeza();
				List<Magistrado> magistradosHabilitados = new ArrayList<Magistrado>(
						lista);
				List<Magistrado> escolhidos = new ArrayList<Magistrado>();
				int tentativasDeSorteio = 0;
				while (escolhidos.size() <= totalDeProcessos) {
					escolhidos = new ArrayList<Magistrado>();
					Iterator<Magistrado> magistrados = magistradosHabilitados
							.iterator();
					while (magistrados.hasNext()) {
						Magistrado magistrado = magistrados.next();
						int numero = random.nextInt(100) + 1;
						if (numero < 51) {
							escolhidos.add(magistrado);
						}
					}
					tentativasDeSorteio++;
					if (tentativasDeSorteio > 100000000) {
						throw new RuntimeException(
								"Tentativas de sorteio ultrapassaram 100.000.000!");
					}

				}
				System.out.println(
						"Tentativas de Sorteio: " + tentativasDeSorteio);
				escolhidos.sort(Magistrado.MagistradoDebitoComparator);
				System.out
						.println("Magistrados sorteados: " + escolhidos.size());

				int quantidadeDeProcessos = totalDeProcessos;
				int chave = 0;
				while (quantidadeDeProcessos > 0) {
					if (quantidadeDeProcessos >= grandeza) {
						int idMagistado = escolhidos.get(chave).getId();
						atualizarControleDoTotalDistribuido(idMagistado,
								grandeza);
						atualizarControleDoTotalDistribuidoDoDia(idMagistado,
								grandeza);
						quantidadeDeProcessos = quantidadeDeProcessos
								- grandeza;
						if (subtrairQuantidadeDeProcessosRecebidos) {
							decrementarDifereca(escolhidos.get(chave),
									grandeza);
						}

					}
					if (quantidadeDeProcessos < grandeza) {
						int idMagistado = escolhidos.get(chave).getId();
						atualizarControleDoTotalDistribuido(idMagistado,
								quantidadeDeProcessos);
						atualizarControleDoTotalDistribuidoDoDia(idMagistado,
								quantidadeDeProcessos);
						quantidadeDeProcessos = 0;
						if (subtrairQuantidadeDeProcessosRecebidos) {
							decrementarDifereca(escolhidos.get(chave),
									quantidadeDeProcessos);
						}
					}

					chave = chave + 1;
					if (chave == escolhidos.size()) {
						chave = 0;
					}
				}

				int quantidadeRecebidaPeloPrimeiroDaLista = controleDoTotalDistribuidoDoDia
						.get(escolhidos.get(0).getId());

				if (somarQuantidadeDeProcessosNaoRecebidos) {
					for (int c = 1; c < escolhidos.size() - 1; c++) {
						int valor = quantidadeRecebidaPeloPrimeiroDaLista
								- controleDoTotalDistribuidoDoDia
										.get(escolhidos.get(c).getId());
						incrementarDifereca(escolhidos.get(c), valor);
					}

				}

				List<Magistrado> magistradosExcluidos = new ArrayList<Magistrado>(
						lista);
				magistradosExcluidos.removeAll(escolhidos);
				if (somarQuantidadeDeProcessosNaoRecebidos) {
					incrementarDifereca(magistradosExcluidos,
							quantidadeRecebidaPeloPrimeiroDaLista);
				}
				Iterator<Magistrado> magistradosEscolhidos = escolhidos
						.iterator();
				while (magistradosEscolhidos.hasNext()) {
					Magistrado magistrado = magistradosEscolhidos.next();
					int total = controleDoTotalDeSorteados
							.get(magistrado.getId());
					total = total + 1;
					controleDoTotalDeSorteados.put(magistrado.getId(), total);
				}
				ajustarControleDaDiferencaParaValoresMinimos(lista);

				System.out.println("Distribuição acumulada: "
						+ controleDoTotalDistribuido);
				System.out.println(
						"Tabela de Diferenças: " + controleDiferencaAtual);
			}

			calculaDistancia();
			System.out.println("");
			System.out.println(
					"-------------------------------------------------------------------");
			System.out.println(
					"ATENÇÃO: SISTEMA NÃO ATINGIU O EQUILÍBRIO APÓS O LIMITE DE "
							+ limiteDeDistribuicoes + " DISTRIBUIÇÕES!");
			System.out.println(
					"-------------------------------------------------------------------");

		} catch (Exception e) {
			System.out.println("");
			System.out.println(
					"-------------------------------------------------------------------");
			System.out.println(e.getMessage());
			System.out.println(
					"-------------------------------------------------------------------");
		} finally {
			System.out.println("");
			System.out.println(
					"-------------------------------------------------------------------");
			System.out.println("RESULTADOS FINAIS DOS TESTES: ");
			System.out.println(
					"-------------------------------------------------------------------");
			System.out.println("");
			System.out.println("Distância Inicial: " + distanciaInicial);
			System.out.println("Distância Final: " + distanciaAtual);
			System.out.println("Total de Magistrados: " + totalDeMagistrados);
			System.out
					.println("Total de Distribuições: " + totalDeDistribuicoes);
			System.out.println(
					"Tabela de Processos Devidos: " + controleDiferencaAtual);
			System.out.println("Total de sorteios de magistrados: "
					+ controleDoTotalDeSorteados);
		}

	}

	private static void atualizarControleDoTotalDistribuido(int idMagistrado,
			int quantidade) {
		Integer total = controleDoTotalDistribuido.get(idMagistrado);
		total = total + quantidade;
		controleDoTotalDistribuido.put(idMagistrado, total);
	}

	private static void carregarControleDoTotalDistribuido() {
		controleDoTotalDistribuido = new HashMap<Integer, Integer>();
		for (int i = 1; i <= totalDeMagistrados; i++) {
			controleDoTotalDistribuido.put(i, 0);
		}
	}

	private static void carregarControleDoTotalDistribuidoDoDia() {
		controleDoTotalDistribuidoDoDia = new HashMap<Integer, Integer>();
		for (int i = 1; i <= totalDeMagistrados; i++) {
			controleDoTotalDistribuidoDoDia.put(i, 0);
		}
	}

	private static void carregarControleDoTotalDeSorteados() {
		controleDoTotalDeSorteados = new HashMap<Integer, Integer>();
		for (int i = 1; i <= totalDeMagistrados; i++) {
			controleDoTotalDeSorteados.put(i, 0);
		}
	}

	private static void incrementarDifereca(List<Magistrado> magistrados,
			int quantidade) {
		Iterator<Magistrado> it = magistrados.iterator();
		while (it.hasNext()) {
			Magistrado magistrado = it.next();
			incrementarDifereca(magistrado, quantidade);

		}

	}

	private static void incrementarDifereca(Magistrado magistrado,
			int quantidade) {
		Integer diferenca = controleDiferencaAtual.get(magistrado.getId());
		diferenca = diferenca + quantidade;
		controleDiferencaAtual.put(magistrado.getId(), diferenca);
	}

	private static void decrementarDifereca(Magistrado magistrado,
			int quantidade) {
		Integer diferenca = controleDiferencaAtual.get(magistrado.getId());
		diferenca = diferenca - quantidade;
		controleDiferencaAtual.put(magistrado.getId(), diferenca);
	}

	private static void ajustarControleDaDiferencaParaValoresMinimos(
			List<Magistrado> lista) {
		Boolean valorMinimoZero = false;
		List<Magistrado> novaLista = new ArrayList<Magistrado>(lista);
		Iterator<Magistrado> magistrados = novaLista.iterator();
		while (magistrados.hasNext()) {
			Magistrado magistrado = magistrados.next();
			Integer diferenca = controleDiferencaAtual.get(magistrado.getId());
			if (diferenca.equals(0)) {
				valorMinimoZero = true;
				break;
			}
		}
		if (!valorMinimoZero) {
			ajustaDifereca(lista);
		}
	}

	private static void ajustaDifereca(List<Magistrado> lista) {
		List<Magistrado> novalista = new ArrayList<Magistrado>(lista);
		Iterator<Magistrado> magistrados = novalista.iterator();
		while (magistrados.hasNext()) {
			Magistrado magistrado = magistrados.next();
			Integer diferenca = controleDiferencaAtual.get(magistrado.getId());
			if (diferenca > 0) {
				diferenca = diferenca - 1;
			} else if (diferenca < 0) {
				diferenca = diferenca + 1;

			}
			controleDiferencaAtual.put(magistrado.getId(), diferenca);
			atualizaListaComTabelaDeDebito();
		}
		ajustarControleDaDiferencaParaValoresMinimos(lista);
	}

	/*
	 * inicializa a tabela de processos devidos com valores de diferenças
	 * aleatórios.
	 */
	private static void carregarControleDiferenca() {
		controleDiferencaAtual = new HashMap<Integer, Integer>();
		Random random = new Random();
		for (int i = 1; i <= totalDeMagistrados; i++) {
			int debito = random.nextInt(sementeDoDesequilibrio);
			if (i % 2 == 0) {
				debito = debito * -1;
			}
			controleDiferencaAtual.put(i, debito);
		}
	}

	private static void atualizarControleDoTotalDistribuidoDoDia(
			int idMagistado, int quantidade) {
		int valor = controleDoTotalDistribuidoDoDia.get(idMagistado);
		valor = valor + quantidade;
		controleDoTotalDistribuidoDoDia.put(idMagistado, valor);

	}

	private static void calcularGrandeza() {
		calculaDistancia();
		double divisao = distanciaAtual / 3;
		double arredondadoParaBaixo = Math.ceil(((double) distanciaAtual / 3));
		if (divisao > arredondadoParaBaixo) {
			grandeza = (int) (arredondadoParaBaixo + 1);
		} else {
			grandeza = (int) arredondadoParaBaixo;
		}
		if (!considerarGrandeza) {
			grandeza = 1;
		}
	}

	private static void calculaDistancia() {
		List<Magistrado> listaParaDiferenca = new ArrayList<Magistrado>(lista);
		listaParaDiferenca.sort(Magistrado.MagistradoDebitoComparator);
		int maiorValor = listaParaDiferenca.get(0).getDebito();
		int menorValor = listaParaDiferenca.get(listaParaDiferenca.size() - 1)
				.getDebito();
		distanciaAtual = maiorValor - menorValor;
	}

	private static void inicializaListaComTabelaDeDebito() {
		lista = new ArrayList<Magistrado>();
		for (int i = 1; i <= totalDeMagistrados; i++) {
			Magistrado magistrado = new Magistrado();
			magistrado.setId(i);
			magistrado.setNome("Magistrado " + i);
			magistrado.setDebito(controleDiferencaAtual.get(i));
			lista.add(magistrado);
		}
	}

	private static void atualizaListaComTabelaDeDebito() {
		for (int i = 1; i <= totalDeMagistrados; i++) {
			Magistrado magistrado = lista.get(i - 1);
			magistrado.setDebito(controleDiferencaAtual.get(i));
		}
	}

}