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

# Débito ou Crédito? 
Para não cair na armadilha da compensação indevida, devemos sempre enxergar a distribuição de processos como crédito. Todas as vezes que um magistrado recebe um processo, está com crédito perante o colegiado. Quando um magistrado deixa de receber um processo não está com crédito, mas também não está com débito. 

Quando o sistema controla a diferença regimental de 3 processos, está controlando os processos que um magistrado recebeu além dos demais, ou seja, está controlando o crédito.  

O crédito é pessoal e intransferível. 

Se um magistrado com crédito de 2 processos se afasta, este crédito deve permanecer quando ele retornar. Se não possuía crédito, retornará sem crédito também. 

Quando um magistrado se afasta, não devemos olhar para o crédito dos outros a fim de compará-los e transformar a diferença de crédito em débito. Na verdade não é necessário fazer coisa alguma, pois o sorteio cuidará de reequilibrar quem está com crédito. Por exemplo: 

O Magistrado A tem crédito de 2 processos.  
O Magistrado B tem crédito de 6 processos (recebeu sucessivos processos por prevenção). 
Se o Magistrado A se afastar, nenhum tipo de cálculo deve ser realizado. O sistema cuidará de distribuir os processos para os demais magistrados durante o afastamento do Magistrado A, de forma a diminuir a diferença que o Magistrado B tem. Assim, se durante o período de afastamento do Magistrado A o sistema equilibrou a distribuição, ou seja, o crédito dos demais magistrados está zero, nada muda em relação ao Magistrado A que continua com seu crédito pessoal de 2 processos.  
O sistema não teve que realizar nenhum cálculo nem qualquer tipo de compensação com o afastamento. 
 
Se o raciocínio for inverso, ou seja, transformar a diferença dos créditos em débito, caímos na armadilha da compensação indevida que o atual modelo implementou. Por exemplo: 

O Magistrado A tem crédito de 2 processos.  
O Magistrado B tem crédito de 6 processos (recebeu sucessivos processos por prevenção). 
Se, erroneamente, quando o Magistrado A se afastar, anotarmos um débito em relação ao maior crédito, converteremos seu crédito de 2 em débito de 4 processos. Assim, se durante o período de afastamento do Magistrado A o sistema equilibrou a distribuição, ou seja, o crédito dos demais magistrados está zero, o Magistrado A será prejudicado com uma compensação indevida pelo afastamento.  

Não é culpa do Magistrado A o fato de o sistema ter distribuído mais ou menos processos para outros magistrados.  A única coisa que importa para o Magistrado A é que seu crédito não pode ser perdido. 
 
Portanto, a diferença é um crédito. Quem tem mais crédito do que deveria, será retirado do sorteio até que o crédito dos demais aumentem e, consequentemente, o crédito do retirado diminua. Este é o procedimento de equilíbrio de créditos.  

Quando há necessidade de redistribuição, não devemos debitar do magistrado o processo que está saindo dele. Isso poderia fazer com que as diferenças (valores entre zero e 3, idealmente) assumissem valores negativos, o que embutiria uma complexidade desnecessária no controle do sistema. Nesta situação basta realizar um controle de "créditos indevidos" que indica quantos processos de uma classe o magistrado redistribuiu.  

Dessa forma, após o sorteio, caso o magistrado tenha algum "crédito indevido", não receberá o crédito pelo processo que acabou de ser distribuído para ele, apenas um decremento no seu "crédito indevido".
