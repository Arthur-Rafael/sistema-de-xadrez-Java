package xadrez;


import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jogoDeTabuleiro.Peca;
import jogoDeTabuleiro.Posicao;
import jogoDeTabuleiro.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.King;
import xadrez.pecas.Peao;
import xadrez.pecas.Queen;
import xadrez.pecas.Torre;

public class PartidaDeXadrez {

	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean xeque;
	private boolean xequeMate;
	private PecaDeXadrez vulneravelAEnPassant;
	private PecaDeXadrez promovida;

	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();
	
	public PartidaDeXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		setupInicial();
	}
	
	public int getTurno() {
		return turno;
	}
	
	public Cor getjogadorAtual() {
		return jogadorAtual;
	}
	
	public boolean getXeque() {
		return xeque;
	}
	
	public boolean getXequeMate() {
		return xequeMate;
	}
	
	public PecaDeXadrez getVulneravelAEnPassant() {
		return vulneravelAEnPassant;
	}
	
	public PecaDeXadrez getPromovida() {
		return promovida;
	}

	public PecaDeXadrez[][] getPecas() {
		PecaDeXadrez[][] mat = new PecaDeXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		for(int i = 0; i < tabuleiro.getLinhas(); i++) {
			for(int j = 0; j < tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaDeXadrez) tabuleiro.peca(i, j);
			}
		}
		return mat;
	}
	
	public boolean[][] movimentosPossiveis(PosicaoDoXadrez posicaoInicial) {
		Posicao posicao = posicaoInicial.toPosicao();
		validaPosicaoInicial(posicao);
		return tabuleiro.peca(posicao).movimentosPossiveis();
	}
	
	public PecaDeXadrez performaMovimento(PosicaoDoXadrez posicaoDeOrigem, PosicaoDoXadrez posicaoDeDestino) {
		Posicao inicial = posicaoDeOrigem.toPosicao();
		Posicao destino = posicaoDeDestino.toPosicao();
		validaPosicaoInicial(inicial);
		validaPosicaoFinal(inicial, destino);
		Peca pecaCapturada = fazMovimento(inicial, destino);
		
		if(testaXeque(jogadorAtual)) {
			desfazMovimento(inicial, destino, pecaCapturada);
			throw new XadrezException("Você não pode se colocar em cheque");
		}
		
		PecaDeXadrez pecaMovida = (PecaDeXadrez)tabuleiro.peca(destino);
		
		// #movimento especial promoção
		promovida = null;
		if(pecaMovida instanceof Peao) {
			if(pecaMovida.getCor() == Cor.BRANCO && destino.getLinha() == 0 || pecaMovida.getCor() == Cor.PRETO && destino.getLinha() == 7) {
				promovida = (PecaDeXadrez)tabuleiro.peca(destino);
				promovida = substituiPecaPromovida("Q");
			}
		}
		
		xeque = (testaXeque(oponente(jogadorAtual))) ? true : false;
		
		if(testaXequeMate(oponente(jogadorAtual))) {
			xequeMate = true;
		} else {
			proximoTurno();
		}
		
		//#Movimento especial enPassant
		if(pecaMovida instanceof Peao && (destino.getLinha() == inicial.getLinha() - 2 || destino.getLinha() == inicial.getLinha() + 2)) {
			vulneravelAEnPassant = pecaMovida;
		} else {
			vulneravelAEnPassant = null;
		}
		
		return (PecaDeXadrez)pecaCapturada;
	}
	
	public PecaDeXadrez substituiPecaPromovida(String tipo) {
		if(promovida == null) {
			throw new IllegalAccessError("Não já peça para ser promovida");
		}
		if(!tipo.equals("T") && !tipo.equals("C") && !tipo.equals("B") && !tipo.equals("Q")) {
			throw new InvalidParameterException("Tipo invalido para ser promovido");
		}
		
		Posicao pos = promovida.getPosicaoDoXadrez().toPosicao(); 
		Peca p = tabuleiro.removePeca(pos);
		pecasNoTabuleiro.remove(p);
		
		PecaDeXadrez novaPeca = novaPeca(tipo, promovida.getCor());
		tabuleiro.colocaPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);
		
		return novaPeca;
	}
	
	private PecaDeXadrez novaPeca(String tipo, Cor cor) {
		if(tipo.equals("B")) return new Bispo(tabuleiro, cor);
		if(tipo.equals("C")) return new Cavalo(tabuleiro, cor);
		if(tipo.equals("T")) return new Torre(tabuleiro, cor);
		return new Queen(tabuleiro, cor);
	}
	
	private Peca fazMovimento(Posicao inicial, Posicao destino) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removePeca(inicial);
		p.acrescentaContadorDeMovimento();
		Peca pecaCapturada = tabuleiro.removePeca(destino);
		tabuleiro.colocaPeca(p, destino);
		
		if(pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		
		// #movimento especial Roque curto(lado do rei)
		if(p instanceof King && destino.getColuna() == inicial.getColuna() + 2) {
			Posicao inicialTorre = new Posicao(inicial.getLinha(), inicial.getColuna() + 3);
			Posicao destinoTorre = new Posicao(inicial.getLinha(), inicial.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(inicialTorre);
			tabuleiro.colocaPeca(torre, destinoTorre);
			torre.acrescentaContadorDeMovimento();
		}
		
		// #movimento especial Roque grande(lado da rainha)
		if(p instanceof King && destino.getColuna() == inicial.getColuna() - 2) {
			Posicao inicialTorre = new Posicao(inicial.getLinha(), inicial.getColuna() - 4);
			Posicao destinoTorre = new Posicao(inicial.getLinha(), inicial.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(inicialTorre);
			tabuleiro.colocaPeca(torre, destinoTorre);
			torre.acrescentaContadorDeMovimento();
		}
		
		// #movimento especial enPassant
		if(p instanceof Peao) {
			if(inicial.getColuna() != destino.getColuna() && pecaCapturada == null) {
				Posicao posicaoDoPeao;
				if(p.getCor() == Cor.BRANCO) {
					posicaoDoPeao = new Posicao(destino.getLinha() + 1, destino.getColuna());
				} else {
					posicaoDoPeao = new Posicao(destino.getLinha() - 1 , destino.getColuna());
				}
				pecaCapturada = tabuleiro.removePeca(posicaoDoPeao);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);
			}
		}
		
		return pecaCapturada;
	}
	
	private void desfazMovimento(Posicao inicial, Posicao destino, Peca pecaCapturada) {
		PecaDeXadrez p = (PecaDeXadrez)tabuleiro.removePeca(destino);
		p.decrementaContadorDeMovimento(); 
		tabuleiro.colocaPeca(p, inicial);
		
		if(pecaCapturada != null) {
			tabuleiro.colocaPeca(pecaCapturada, destino);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
		
		// #movimento especial Roque curto(lado do rei)
		if(p instanceof King && destino.getColuna() == inicial.getColuna() + 2) {
			Posicao inicialTorre = new Posicao(inicial.getLinha(), inicial.getColuna() + 3);
			Posicao destinoTorre = new Posicao(inicial.getLinha(), inicial.getColuna() + 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(inicialTorre);
			tabuleiro.colocaPeca(torre, inicialTorre);
			torre.decrementaContadorDeMovimento();
		}
		
		// #movimento especial Roque grande(lado da rainha)
		if(p instanceof King && destino.getColuna() == inicial.getColuna() - 2) {
			Posicao inicialTorre = new Posicao(inicial.getLinha(), inicial.getColuna() - 4);
			Posicao destinoTorre = new Posicao(inicial.getLinha(), inicial.getColuna() - 1);
			PecaDeXadrez torre = (PecaDeXadrez)tabuleiro.removePeca(inicialTorre);
			tabuleiro.colocaPeca(torre, inicialTorre);
			torre.decrementaContadorDeMovimento();
		}
		
		// #movimento especial enPassant
		if(p instanceof Peao) {
			if(inicial.getColuna() != destino.getColuna() && pecaCapturada == vulneravelAEnPassant) {
				Posicao posicaoDoPeao;
				PecaDeXadrez peao = (PecaDeXadrez)tabuleiro.removePeca(destino);
				if(p.getCor() == Cor.BRANCO) {
					posicaoDoPeao = new Posicao(3, destino.getColuna());
				} else {
					posicaoDoPeao = new Posicao(4, destino.getColuna());
				}
				tabuleiro.colocaPeca(peao, posicaoDoPeao);
				
				pecaCapturada = tabuleiro.removePeca(posicaoDoPeao);
			}
		}
	}
	
	private void validaPosicaoInicial(Posicao posicao) {
		if (!tabuleiro.haUmaPeca(posicao)) {
			throw new XadrezException("Não existe peça na posição de origem");
		}
		if(jogadorAtual != ((PecaDeXadrez)tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezException("A peça escolhida não é sua");
		}
		if (!tabuleiro.peca(posicao).temAlgumMovimentoPossivel()) {
			throw new XadrezException("Não tem movimentos possiveis para a peça selecionada");
		}
	}
	
	private void validaPosicaoFinal(Posicao inicial, Posicao destino) {
		if(!tabuleiro.peca(inicial).movimentoPossivel(destino)) {
			throw new XadrezException("A peça escolhida não pode se mover para a posição de destino");
		}
	}
	
	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}
	
	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}
	
	private PecaDeXadrez King(Cor cor) {
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == cor).collect(Collectors.toList());
		for (Peca p : lista) {
			if(p instanceof King) {
				return (PecaDeXadrez) p;
			}
		}
		throw new IllegalStateException("Não tem o King " + cor + " no tabuleiro");
	}
	
	private boolean testaXeque(Cor cor) {
		Posicao posicaoDoRei = King(cor).getPosicaoDoXadrez().toPosicao(); 
		List<Peca> pecasOponentes = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == oponente(cor)).collect(Collectors.toList());
		for (Peca p : pecasOponentes) {
			boolean[][] mat = p.movimentosPossiveis();
			if(mat[posicaoDoRei.getLinha()][posicaoDoRei.getColuna()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testaXequeMate(Cor cor) {
		if(!testaXeque(cor)) {
			return false;
		}
		List<Peca> lista = pecasNoTabuleiro.stream().filter(x -> ((PecaDeXadrez)x).getCor() == cor).collect(Collectors.toList());
		for (Peca p : lista) {
			boolean[][] mat = p.movimentosPossiveis();
			for(int i = 0; i < tabuleiro.getLinhas(); i++) {
				for(int j = 0; j < tabuleiro.getColunas(); j++) {
					if(mat[i][j]) {
						Posicao inicial = ((PecaDeXadrez)p).getPosicaoDoXadrez().toPosicao();
						Posicao destino = new Posicao(i, j);
						Peca pecaCapturada = fazMovimento(inicial, destino);
						boolean testaXeque = testaXeque(cor);
						desfazMovimento(inicial, destino, pecaCapturada);
						if(!testaXeque) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void colocaNovaPeca(char coluna, int linha, PecaDeXadrez peca) {
		tabuleiro.colocaPeca(peca, new PosicaoDoXadrez(coluna, linha).toPosicao());
		pecasNoTabuleiro.add(peca);
	}
	
	private void setupInicial() {
		colocaNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('d', 1, new Queen(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('e', 1, new King(tabuleiro, Cor.BRANCO, this));

		colocaNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		colocaNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		colocaNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));
		colocaNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));
		colocaNovaPeca('d', 8, new Queen(tabuleiro, Cor.PRETO));
		colocaNovaPeca('e', 8, new King(tabuleiro, Cor.PRETO, this));
	}
}
