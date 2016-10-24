# Distribuição
Proposta de solução de distribuição de processos por classe

Esta é uma implementação didática da solução para demonstrar o funcionamento da distribuição processual por classes controlada pela alternatividade e proporcionalidade.
A alternatividade é garantida com a retirada do último magistrado sorteado na classe processual.
A proporcionalidade é garantida com o controle da Diferença de processos distribuídos. Assim, os magistrados que são sorteados e atingem o limite estabelecido estão fora dos próximos sorteios até que todos recebam processos e diminuam a diferença entre si. 
O ponto chave da solução é manter os valores das diferenças sempre entre zero e o limite máximo (3 processos).
Assim, quando todos os magistrados que estão participando do sorteio estiverem com valores acima de zero, o sistema ajustará os valores para baixo novamente. Isso impedirá que os valores se acumulem indiscriminadamente. Essa premissa de manter os valores sempre baixos impedirá que haja uma diferença com os magistrados que se afastam ou com aqueles que entram no órgão.

Forma de utilização do programa:
Para compilar a classe Distribuicao.java execute: javac Distribuicao.java
A classe Distribuicao.java, quando compilada produzirá os binários:
Distribuicao.class e Magistrado.class.

Para testar uma distribuição, execute o seguinte comando na pasta em que estiverem os arquivos Distribuicao.class e Magistrado.class:

java Distribuicao {quantidadeDeMagistrados} {quantidadeDeProcessos}

exemplo: para distribuir 50 processos entre 15 magistrados execute

java Distribuicao 15 50
