package br.jus.distribuicao;

import java.util.*;

public class Distribuicao {
	public int totalDeMagistradosDoOrgao;
	public int totalDeProcessosParaDistribuir;
	private Magistrado ultimoMagistradoSorteado;
	private Magistrado magistradoSorteado;
	private List<Integer> processosDaClasseParaDistribuir;
	private List<Magistrado> magistradosDoOrgao;
	private List<Magistrado> magistradosDoOrgaoSemAfastados;
	private List<Magistrado> magistradosHabilitadosParaSorteio;
	public HashMap<Integer, Integer> controleDiferenca;
	public HashMap<Integer, Integer> controleDoTotalDistribuido;
	public HashMap<Integer, String> processosDistribuidos;

	/**
	 * Esta solução permite a distribuição para um processo ou para um lote. Na
	 * verdade, não faz diferença a quantidade de processos. Para cada processo
	 * distribuído é realizado o controle da Diferença entre magistrados na
	 * classe processual em questão. A Diferença é uma variável que é
	 * incrementada cada vez que o magistrado é sorteado. Esta variável é
	 * controlada para cada classe de forma distinta. Assim, haverá uma tabela
	 * no banco de dados para vincular o valores das diferenças entre
	 * magistrados em cada uma das classes processuais. Haverá um controle nesta
	 * variável da Diferença para manter os valores sempre baixos (entre 0 e 3)
	 * com exceção dos casos de prevenção de magistrado. Este procedimento de
	 * manter os valores diferenciais baixos impedirá a desproporcionalidade
	 * entre magistrados, principalmente para os casos de afastamentos e de
	 * entrada no órgão. Não foi o propósito deste exemplo de solução o
	 * esgotamento de todas as regras necessárias, por exemplo, a redistribuição
	 * em caso de suspeição ou impedimento, etc., a compensação de processos nas
	 * turmas quando o magistrado recebe processo do Conselho Especial e Câmara
	 * de Uniformização, bem como a compensação de processos redistribuídos em
	 * caso de urgência. Para cada caso, deve-se avaliar a necessidade de
	 * ajustar a o valor da Diferença do magistrado afetado.
	 */
	public void distribuir() {
		carregarProcessosDaClasseParaDistribuir();
		carregarControleDiferenca();
		carregarControleDoTotalDistribuido();
		Iterator<Integer> processos = processosDaClasseParaDistribuir
				.iterator();
		processosDistribuidos = new HashMap<Integer, String>();
		while (processos.hasNext()) {
			sortearMagistrado();
			Integer processo = processos.next();
			processosDistribuidos.put(processo, magistradoSorteado.getNome());
			System.out.println("Processo número " + processo + ": "
					+ magistradoSorteado.getNome());
		}
	}

	/**
	 * Com a lista de magistrados do órgão, é realizada a remoção dos
	 * magistrados afastados. em seguida, o último magistrado sorteado na classe
	 * é retirado do sorteio. Depois, os magistrados que estão no limite da
	 * Diferença também são excluídos do sorteio. Com a lista de magistrados
	 * remanescente, o sorteio é realizado. Não foi implementado o controle de
	 * impedimento. No entanto, deve-se seguir a mesma linha de pensamento para
	 * remover os magistrados impedidos. Após o sorteio, o o magistrado sorteado
	 * é indicado como último para remoção no sorteio seguinte. Por fim, o
	 * controle da Diferença é iniciado com o incremento no valor do magistrado
	 * sorteado.
	 */
	private void sortearMagistrado() {
		this.magistradosDoOrgao = carregarMagistradosDoOrgao();
		this.magistradosDoOrgaoSemAfastados = removerMagistradosAfastados();
		this.magistradosHabilitadosParaSorteio = removerUltimoMagistradoSorteado();
		removerMagistradosComDiferencaRegimental();
		Collections.shuffle(magistradosHabilitadosParaSorteio);
		magistradoSorteado = magistradosHabilitadosParaSorteio.get(0);
		atualizarControleDoTotalDistribuido();
		incrementarDifereca();
		ultimoMagistradoSorteado = magistradoSorteado;

	}

	/**
	 * verifica os afastamentos. não implementado aqui para demonstração da
	 * solução. Os afastados não terão seu valor de Diferença alterado. Assim,
	 * quando retornar, seu valor estará da mesma forma quando saiu, em
	 * comparação com os demais. Vale lembrar que esses valores não tendem a
	 * sair do intervalo entre 0 (zero) e 3 (três), com exceção da atribuição
	 * por prevenção de magistrado, que pode ser sequencial e ultrapassar mais
	 * que o limite da diferença (3 processos). Neste caso, o magistrado também
	 * será retirado do sorteio enquanto sua diferença não estiver dentro do
	 * limite.
	 * 
	 */
	private List<Magistrado> removerMagistradosAfastados() {
		List<Magistrado> lista = new ArrayList<Magistrado>(
				this.magistradosDoOrgao);
		Iterator<Magistrado> magistrados = lista.iterator();
		while (magistrados.hasNext()) {
			Magistrado magistrado = magistrados.next();
			// TODO remover os afastados (férias, licenças, etc.)
		}
		return lista;
	}

	/**
	 * garante que não será desresteidada a Alternatividade imposta no Regimento
	 * Interno. O último magistrado sorteado na classe deve estar persistido em
	 * banco de dados para ser removido da lista de magistrados disponíveis para
	 * a distribuição (não afastados)
	 * 
	 */
	private List<Magistrado> removerUltimoMagistradoSorteado() {
		List<Magistrado> lista = new ArrayList<Magistrado>(
				this.magistradosDoOrgaoSemAfastados);
		if (ultimoMagistradoSorteado != null) {
			lista.remove(ultimoMagistradoSorteado);
		}
		return lista;
	}

	/**
	 * O Regimento Interno determina que não pode haver diferença superior a 3
	 * processos. Portanto, o magistrado que já tem diferença de 3 processos ou
	 * mais não pode participar so sorteio. Neste ponto do programa, já foi
	 * realizado o ajuste da Diferença e o decremento dos valores. Assume-se que
	 * as diferenças estão ajustadas com valores mínimos igual a zero.
	 * 
	 */
	private void removerMagistradosComDiferencaRegimental() {
		List<Magistrado> lista = new ArrayList<Magistrado>(
				this.magistradosHabilitadosParaSorteio);
		Iterator<Magistrado> magistrados = lista.iterator();
		while (magistrados.hasNext()) {
			Magistrado magistrado = magistrados.next();
			Integer diferenca = this.controleDiferenca.get(magistrado.getId());
			if (diferenca.intValue() >= 3) {
				this.magistradosHabilitadosParaSorteio.remove(magistrado);
			}
		}
	}

	/**
	 * controle do total de processos distribuídos. utilizado aqui apenas para
	 * fins de demonstração.
	 */
	private void atualizarControleDoTotalDistribuido() {
		Integer total = controleDoTotalDistribuido
				.get(magistradoSorteado.getId());
		total = total + 1;
		controleDoTotalDistribuido.put(magistradoSorteado.getId(), total);
	}

	/**
	 * A "Diferença" é a variável utilizada para identificar quais magistrados
	 * estão sendo sorteados mais vezes, e assim, permite que se retire do
	 * sorteio do processo aqueles que já estão com uma diferença igual ou
	 * superior a 3 processos em comparação com os magistrados que estão
	 * participando da distribuição.
	 */
	private void incrementarDifereca() {
		Integer diferenca = controleDiferenca.get(magistradoSorteado.getId());
		diferenca = diferenca + 1;
		controleDiferenca.put(magistradoSorteado.getId(), diferenca);
		ajustarControleDaDiferencaParaValoresMinimos();
	}

	/**
	 * Verifica se todos os magistrados não afastados do órgão já receberam um
	 * incremento no valor da Diferença. Se algum ainda não recebeu (seu valor
	 * continua zero) então não faz nada. Caso contrário, se todos já estão no
	 * mínio com valor 1 (um), decrementa todos os valores dos magistrados não
	 * afastados em 1(uma) unidade. Isso garante que não haverá problemas de
	 * compensação quando em magistrado entrar no órgão ou um afastado retornar
	 * para a atividade.
	 */
	private void ajustarControleDaDiferencaParaValoresMinimos() {
		Boolean valorMinimoZero = false;
		List<Magistrado> lista = new ArrayList<Magistrado>(
				this.magistradosDoOrgaoSemAfastados);
		Iterator<Magistrado> magistrados = lista.iterator();
		while (magistrados.hasNext()) {
			Magistrado magistrado = magistrados.next();
			Integer diferenca = this.controleDiferenca.get(magistrado.getId());
			if (diferenca.equals(0)) {
				valorMinimoZero = true;
				break;
			}
		}
		if (!valorMinimoZero) {
			decrementarDifereca();
		}
	}

	/**
	 * Em Produção, persiste o valor decrementado da diferença na tabela do
	 * banco de dados
	 */
	private void decrementarDifereca() {
		List<Magistrado> lista = new ArrayList<Magistrado>(
				this.magistradosDoOrgaoSemAfastados);
		Iterator<Magistrado> magistrados = lista.iterator();
		while (magistrados.hasNext()) {
			Magistrado magistrado = magistrados.next();
			Integer diferenca = this.controleDiferenca.get(magistrado.getId());
			diferenca = diferenca - 1;
			controleDiferenca.put(magistrado.getId(), diferenca);
		}
		ajustarControleDaDiferencaParaValoresMinimos();
	}

	private void carregarProcessosDaClasseParaDistribuir() {
		this.processosDaClasseParaDistribuir = new ArrayList<Integer>();
		for (int i = 1; i <= totalDeProcessosParaDistribuir; i++) {
			this.processosDaClasseParaDistribuir.add(i);

		}
	}

	/**
	 * Para fins de demonstração, as diferenças dos magistrados iniciam com
	 * zero. Em Produção os valores são recuperados da tabela do banco de dados
	 * que armazena as diferenças dos magistrados para a Classe Processual do
	 * processo que está sendo distribuído.
	 */
	private void carregarControleDiferenca() {
		this.controleDiferenca = new HashMap<Integer, Integer>();
		for (int i = 1; i <= totalDeMagistradosDoOrgao; i++) {
			this.controleDiferenca.put(i, 0);
		}
	}

	/**
	 * Controle apenas para fins de apresentação da solução. Demontra a
	 * quantidade de processos distribuídos por magistrados. Comprova a
	 * proporcionalidade da distribuição.
	 */
	private void carregarControleDoTotalDistribuido() {
		this.controleDoTotalDistribuido = new HashMap<Integer, Integer>();
		for (int i = 1; i <= totalDeMagistradosDoOrgao; i++) {
			this.controleDoTotalDistribuido.put(i, 0);
		}
	}

	private List<Magistrado> carregarMagistradosDoOrgao() {
		List<Magistrado> lista = new ArrayList<Magistrado>();
		for (int i = 1; i <= totalDeMagistradosDoOrgao; i++) {
			Magistrado magistrado = new Magistrado();
			magistrado.setId(i);
			magistrado.setNome("Magistrado " + i);
			lista.add(magistrado);
		}
		return lista;
	}

	public static void main(String[] args) {
		int totalDeMagistradosDoOrgao = 40;
		int totalDeProcessosParaDistribuir = 250;

		Distribuicao d = new Distribuicao();
		d.totalDeMagistradosDoOrgao = totalDeMagistradosDoOrgao;
		d.totalDeProcessosParaDistribuir = totalDeProcessosParaDistribuir;
		d.distribuir();
		System.out.println("----------------------------------------");
		System.out.println("Total de Processos Distribuídos por Magistrado:");
		for (int i = 1; i <= totalDeMagistradosDoOrgao; i++) {
			System.out.println("Magistrado " + i + ": "
					+ d.controleDoTotalDistribuido.get(i));
		}
		System.out.println("----------------------------------------");
		System.out.println("Controle de Diferença entre Magistrados:");
		for (int i = 1; i <= totalDeMagistradosDoOrgao; i++) {
			System.out.println(
					"Magistrado " + i + ": " + d.controleDiferenca.get(i));
		}
	}

}
