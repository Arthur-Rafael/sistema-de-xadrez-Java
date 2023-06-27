package xadrez.pecas;

import jogoDeTabuleiro.Posicao;
import jogoDeTabuleiro.Tabuleiro;
import xadrez.Cor;
import xadrez.PartidaDeXadrez;
import xadrez.PecaDeXadrez;

public class King extends PecaDeXadrez{
	
	private PartidaDeXadrez partidaDeXadrez;

	public King(Tabuleiro tabuleiro, Cor cor, PartidaDeXadrez partidaDeXadrez) {
		super(tabuleiro, cor);
		this.partidaDeXadrez = partidaDeXadrez;
	}

	@Override
	public String toString() {
		return "K";
	}

	private boolean podeMover(Posicao posicao) {
		PecaDeXadrez p = (PecaDeXadrez) getTabuleiro().peca(posicao);
		return p == null || p.getCor() != getCor();
	}
	
	private boolean testaTorreParaRoque(Posicao posicao) {
		PecaDeXadrez p = (PecaDeXadrez) getTabuleiro().peca(posicao);
		return p != null && p instanceof Torre && p.getCor() == getCor() && p.getContadorDeMovimento() == 0;
	}
	
	@Override
	public boolean[][] movimentosPossiveis() {
		boolean[][] mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];
		
		Posicao p = new Posicao(0, 0);
		
		// acima
		p.setValores(posicao.getLinha() - 1, posicao.getColuna());
		if(getTabuleiro().posicaoExiste(p) && podeMover(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// abaixo
		p.setValores(posicao.getLinha() + 1, posicao.getColuna());
		if(getTabuleiro().posicaoExiste(p) && podeMover(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// esquerda
		p.setValores(posicao.getLinha(), posicao.getColuna() - 1);
		if(getTabuleiro().posicaoExiste(p) && podeMover(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// direita
		p.setValores(posicao.getLinha(), posicao.getColuna() + 1);
		if(getTabuleiro().posicaoExiste(p) && podeMover(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// NO
		p.setValores(posicao.getLinha() - 1, posicao.getColuna() - 1);
		if(getTabuleiro().posicaoExiste(p) && podeMover(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// NE
		p.setValores(posicao.getLinha() - 1, posicao.getColuna() + 1);
		if(getTabuleiro().posicaoExiste(p) && podeMover(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}

		// SO
		p.setValores(posicao.getLinha() + 1, posicao.getColuna() - 1);
		if(getTabuleiro().posicaoExiste(p) && podeMover(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// SE
		p.setValores(posicao.getLinha() + 1, posicao.getColuna() + 1);
		if(getTabuleiro().posicaoExiste(p) && podeMover(p)) {
			mat[p.getLinha()][p.getColuna()] = true;
		}
		
		// #Movimento especial Roque
		if (getContadorDeMovimento() == 0 && !partidaDeXadrez.getXeque()) {
			// #Movimento especial Roque curto(Roque pelo lado do rei)
			Posicao posT1 = new Posicao(posicao.getLinha(), posicao.getColuna() + 3);
			if(testaTorreParaRoque(posT1)) {
				Posicao p1 = new Posicao(posicao.getLinha(), posicao.getColuna() + 1);
				Posicao p2 = new Posicao(posicao.getLinha(), posicao.getColuna() + 2);
				if(getTabuleiro().peca(p1) == null && getTabuleiro().peca(p2) == null) {
					mat[posicao.getLinha()][posicao.getColuna() + 2] = true;
				}
			}
			// #Movimento especial Roque grande(Roque pelo lado da rainha)
			Posicao posT2 = new Posicao(posicao.getLinha(), posicao.getColuna() - 4);
			if(testaTorreParaRoque(posT2)) {
				Posicao p1 = new Posicao(posicao.getLinha(), posicao.getColuna() - 1);
				Posicao p2 = new Posicao(posicao.getLinha(), posicao.getColuna() - 2);
				Posicao p3 = new Posicao(posicao.getLinha(), posicao.getColuna() - 3);
				if(getTabuleiro().peca(p1) == null && getTabuleiro().peca(p2) == null && getTabuleiro().peca(p3) == null) {
					mat[posicao.getLinha()][posicao.getColuna() - 2] = true;
				}
			}
		}

		return mat;
	}
}
