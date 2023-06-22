package aplicacao;

import java.util.Scanner;

import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;
import xadrez.PosicaoDoXadrez;

public class Programa {

	public static void main(String[] args) {
		
		Scanner sc = new Scanner(System.in);
		PartidaDeXadrez partida = new PartidaDeXadrez();
		
		while(true) {
			IU.printTabuleiro(partida.getPecas());
			System.out.println();
			System.out.print("Posição inicial: ");
			PosicaoDoXadrez inicial = IU.lePosicaoXadrez(sc);
			
			System.out.println();
			System.out.print("Destino: ");
			PosicaoDoXadrez destino = IU.lePosicaoXadrez(sc);
			
			PecaDeXadrez pecaCapturada = partida.performaMovimento(inicial, destino);
		}
		
	}
}
