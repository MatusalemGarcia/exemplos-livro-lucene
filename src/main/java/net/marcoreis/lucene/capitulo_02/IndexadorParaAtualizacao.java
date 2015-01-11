package net.marcoreis.lucene.capitulo_02;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IndexadorParaAtualizacao {
    private static String DIRETORIO_INDICE = System.getProperty("user.home")
            + "/livro-lucene/indice-capitulo-02-exemplo-02";
    private static final Logger logger = Logger
            .getLogger(IndexadorParaAtualizacao.class);
    private IndexWriter writer;

    @Before
    public void inicializar() throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,
                analyzer);
        Directory dir = FSDirectory.open(new File(DIRETORIO_INDICE));
        writer = new IndexWriter(dir, config);
    }

    @After
    public void finalizar() throws IOException {
        writer.close();
    }

    @Test
    public void _02_excluir() throws IOException {
        Term termo = new Term("nome", "Brasil");
        writer.deleteDocuments(termo);
        Assert.assertTrue(writer.maxDoc() == 2);
        Assert.assertTrue(writer.numDocs() == 2);
    }

    @Test
    public void _01_indexar() throws IOException {
        try {
            Document doc = new Document();
            doc.add(new TextField("nome", "Brasil", Store.YES));
            writer.addDocument(doc);
            //
            doc = new Document();
            doc.add(new TextField("nome", "Argentina", Store.YES));
            writer.addDocument(doc);
            //
            Assert.assertTrue(writer.maxDoc() == 2);
            Assert.assertTrue(writer.numDocs() == 2);
            //
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
