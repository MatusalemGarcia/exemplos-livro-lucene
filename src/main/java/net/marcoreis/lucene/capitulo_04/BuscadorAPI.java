package net.marcoreis.lucene.capitulo_04;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class BuscadorAPI {
	// private static String DIRETORIO_INDICE = System.getProperty("user.home")
	// + "/livro-lucene/indice-capitulo-02-exemplo-01";
	private static String DIRETORIO_INDICE = System.getProperty("user.home") + "/livro-lucene/indice";
	private static final Logger logger = Logger.getLogger(BuscadorAPI.class);
	private Directory diretorio;
	private IndexReader reader;
	private IndexSearcher searcher;
	private int QUANTIDADE_DE_ITENS_RETORNADOS = 100;

	//

	public void fechar() {
		try {
			diretorio.close();
			reader.close();
		} catch (IOException e) {
			logger.error(e);
		}
	}

	private void buscar(Query query) {
		try {
			TopDocs docs = searcher.search(query, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Arquivo: " + doc.get("nome"));
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Atualização: " + doc.get("data"));
			}
			//
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}

	}

	private void buscarMatchAllDocs() {
		try {
			MatchAllDocsQuery all = new MatchAllDocsQuery();
			TopDocs docs = searcher.search(all, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Arquivo: " + doc.get("nome"));
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Atualização: " + doc.get("dataAtualizacao"));
			}
			//
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void abrirIndice() {
		try {
			diretorio = FSDirectory.open(Paths.get(DIRETORIO_INDICE));
			reader = DirectoryReader.open(diretorio);
			searcher = new IndexSearcher(reader);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void buscarCampoNumerico() {
		try {
			// NumericRangeQuery<Long> nrq =
			// NumericRangeQuery.newLongRange("tamanho", UM_MEGA, DOIS_MEGAS,
			// true, true);
			TopDocs docs = searcher.search(null, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Arquivo: " + doc.get("nome"));
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Atualização: " + doc.get("dataAtualizacao"));
			}
			//
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void buscarTermQuery() {
		try {
			Term term = new Term("conteudo", "extrair");
			Query query = new TermQuery(term);
			TopDocs docs = searcher.search(query, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Caminho: " + doc.get("caminho"));
				logger.info("Data: " + doc.get("data"));
				logger.info("Extensão: " + doc.get("extensao"));
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void buscarPhraseQuery() {
		try {
			PhraseQuery query = new PhraseQuery("conteudo", "ciência", "da", "informação");
			TopDocs docs = searcher.search(query, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Caminho: " + doc.get("caminho"));
				logger.info("Data: " + doc.get("data"));
				logger.info("Extensão: " + doc.get("extensao"));
			}
			fechar();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void buscarMultiPhraseQuery() {
		try {
			Term[] termosAplicacaoExemplo = new Term[] { new Term("conteudo", "aplicação"),
					new Term("conteudo", "exemplo") };
			Term[] termosBancoDados = new Term[] { new Term("conteudo", "banco"), new Term("conteudo", "dados") };
			//
			// MultiPhraseQuery mpq = new MultiPhraseQuery();
			// mpq.add(termosAplicacaoExemplo);
			// mpq.add(termosBancoDados);
			// mpq.setSlop(2);
			//
			TopDocs docs = searcher.search(null, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Arquivo: " + doc.get("nome"));
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Atualização: " + doc.get("dataAtualizacao"));
			}
			//
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void buscarPrefixQuery() {
		try {
			Term termo = new Term("conteudo", "consider");
			PrefixQuery pq = new PrefixQuery(termo);
			TopDocs docs = searcher.search(pq, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Arquivo: " + doc.get("nome"));
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Atualização: " + doc.get("dataAtualizacao"));
			}
			//
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void buscarSpanQuery() {
		try {
			Term termo = new Term("conteudo", "consider");
			SpanQuery query1;
			SpanQuery[] clausulas = null;
			SpanNearQuery sq = new SpanNearQuery(clausulas, 2, true);
			TopDocs docs = searcher.search(sq, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Arquivo: " + doc.get("nome"));
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Atualização: " + doc.get("dataAtualizacao"));
			}
			//
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void buscarBooleanQuery() {
		try {
			BooleanQuery bq = null;// new BooleanQuery();
			TermQuery tq = new TermQuery(new Term("conteudo", "calor"));
			TermQuery tq1 = new TermQuery(new Term("conteudo", "java"));
			PhraseQuery pq = null;// new PhraseQuery();
			//
			TopDocs docs = searcher.search(bq, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Arquivo: " + doc.get("nome"));
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Atualização: " + doc.get("dataAtualizacao"));
			}
			//
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void buscarWildcardQuery() {
		try {
			Term termo = new Term("conteudo", "calor");
			WildcardQuery wq = new WildcardQuery(termo);
			//
			TopDocs docs = searcher.search(wq, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Arquivo: " + doc.get("nome"));
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Atualização: " + doc.get("dataAtualizacao"));
			}
			//
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void buscarFuzzyQuery() {
		try {
			Term termo = new Term("conteudo", "seção");
			FuzzyQuery fq = new FuzzyQuery(termo, 2);
			logger.info("Query: " + fq);
			//
			TopDocs docs = searcher.search(fq, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Arquivo: " + doc.get("nome"));
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Atualização: " + doc.get("dataAtualizacao"));
			}
			//
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void buscarRangeQuery() {
		try {
			Query q = LongPoint.newRangeQuery("tamanhoLong", 8000, 40000);
			TopDocs docs = searcher.search(q, QUANTIDADE_DE_ITENS_RETORNADOS);
			logger.info("Quantidade de itens encontrados: " + docs.totalHits);
			for (ScoreDoc sd : docs.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				logger.info("Arquivo: " + doc.get("caminho"));
				logger.info("Tamanho: " + doc.get("tamanho"));
				logger.info("Atualização: " + doc.get("data"));
			}
			//
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public long count(final Query qry) {
		long outCount = 0;
		// try {
		// _searchManager.waitForGeneration(_reopenToken); // wait untill the
		// // index is
		// // re-opened
		// IndexSearcher searcher = _searchManager.acquire();
		// try {
		// TopDocs docs = searcher.search(qry, 0);
		// if (docs != null)
		// outCount = docs.totalHits;
		// log.debug("count-search executed against lucene index returning {}",
		// outCount);
		// } finally {
		// _searchManager.release(searcher);
		// }
		// } catch (IOException ioEx) {
		// log.error("Error re-opening the index {}", ioEx.getMessage(), ioEx);
		// }
		return outCount;
	}

}