package aplicacao;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoDoXadrez;
import xadrez.XadrezException;

public class Programa {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaDeXadrez partida = new PartidaDeXadrez();
		List<PecaDeXadrez> capturadas = new ArrayList<>();
		
		while(!partida.getXequeMate()) {
				try {
					IU.limparTela();
					IU.printPartida(partida, capturadas);
					System.out.println();
					System.out.print("Posição inicial: ");
					PosicaoDoXadrez inicial = IU.lePosicaoXadrez(sc);
					
					boolean[][] movimentosPossiveis = partida.movimentosPossiveis(inicial);
					IU.limparTela();
					IU.printTabuleiro(partida.getPecas(), movimentosPossiveis);
					System.out.println();
					System.out.print("Destino: ");
					PosicaoDoXadrez destino = IU.lePosicaoXadrez(sc);
					
					PecaDeXadrez pecaCapturada = partida.performaMovimento(inicial, destino);
					
					if(pecaCapturada != null) {
						capturadas.add(pecaCapturada);
					}
				}
			 catch(XadrezException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} catch(InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		IU.limparTela();
		IU.printPartida(partida, capturadas);
	}
}