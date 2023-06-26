package aplicacao;

import java.util.InputMismatchException;
import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoDoXadrez;
import xadrez.XadrezException;

public class Programa {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaDeXadrez partida = new PartidaDeXadrez();
		
		while(true) {
			try {
				IU.limparTela();
				IU.printPartida(partida);
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
			}
		 catch(XadrezException e) {
			System.out.println(e.getMessage());
			sc.nextLine();
		} catch(InputMismatchException e) {
			System.out.println(e.getMessage());
			sc.nextLine();
		}
	}
	}
}