package net.marcoreis.lucene.capitulo_02;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class IndexadorDadosDeputadosSAXParser extends DefaultHandler {
    public static final String DIRETORIO_INDICE_LEGISLATIVO = System
            .getProperty("user.home")
            + "/livro-lucene/indice-capitulo-02-exemplo-03";
    private static InputStream ARQUIVO_DADOS = IndexadorDadosDeputadosSAXParser.class
            .getClassLoader().getResourceAsStream("dados/ObterDeputados.xml");
    private static Logger logger = Logger
            .getLogger(IndexadorDadosDeputadosSAXParser.class);
    private StringBuilder content = new StringBuilder();
    private IndexWriter writer;
    private Document parlamentar;

    @Before
    public void inicializar() throws IOException {
        Directory dir = FSDirectory
                .open(new File(DIRETORIO_INDICE_LEGISLATIVO));
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_48,
                analyzer);
        writer = new IndexWriter(dir, conf);
    }

    @After
    public void finalizar() throws IOException {
        writer.close();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if (qName.equals("deputado")) {
            parlamentar = new Document();
            content.setLength(0);
        } else if (qName.equals("ideCadastro")) {
            content.setLength(0);
        } else if (qName.equals("condicao")) {
            content.setLength(0);
        } else if (qName.equals("matricula")) {
            content.setLength(0);
        } else if (qName.equals("idParlamentar")) {
            content.setLength(0);
        } else if (qName.equals("nome")) {
            content.setLength(0);
        } else if (qName.equals("nomeParlamentar")) {
            content.setLength(0);
        } else if (qName.equals("urlFoto")) {
            content.setLength(0);
        } else if (qName.equals("sexo")) {
            content.setLength(0);
        } else if (qName.equals("uf")) {
            content.setLength(0);
        } else if (qName.equals("partido")) {
            content.setLength(0);
        } else if (qName.equals("comissao")) {
            content.setLength(0);
            content.append(attributes.getValue("nome"));
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (parlamentar != null) {
            content.append(String.copyValueOf(ch, start, length).trim());
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("deputado")) {
            //
            try {
                writer.addDocument(parlamentar);
            } catch (IOException e) {
                logger.error(e);
            }
            //
            parlamentar = null;
        } else if (qName.equals("ideCadastro")) {
            parlamentar.add(new IntField("ideCadastro", Integer
                    .parseInt(content.toString()), Store.YES));
        } else if (qName.equals("condicao")) {
            parlamentar.add(new StringField("condicao", content.toString(),
                    Store.YES));
        } else if (qName.equals("matricula")) {
            parlamentar.add(new IntField("matricula", Integer.parseInt(content
                    .toString()), Store.YES));
        } else if (qName.equals("idParlamentar")) {
            parlamentar.add(new IntField("idParlamentar", Integer
                    .parseInt(content.toString()), Store.YES));
        } else if (qName.equals("nome")) {
            parlamentar
                    .add(new TextField("nome", content.toString(), Store.YES));
        } else if (qName.equals("nomeParlamentar")) {
            parlamentar.add(new TextField("nomeParlamentar",
                    content.toString(), Store.YES));
        } else if (qName.equals("urlFoto")) {
            parlamentar.add(new StringField("urlFoto", content.toString(),
                    Store.YES));
        } else if (qName.equals("sexo")) {
            parlamentar.add(new StringField("sexo", content.toString(),
                    Store.YES));
        } else if (qName.equals("uf")) {
            parlamentar
                    .add(new StringField("uf", content.toString(), Store.YES));
        } else if (qName.equals("partido")) {
            parlamentar.add(new StringField("partido", content.toString(),
                    Store.YES));
        } else if (qName.equals("comissao")) {
            parlamentar.add(new TextField("comissao", content.toString(),
                    Store.YES));
        }
    }

    @Test
    public void indexar() throws ParserConfigurationException, SAXException,
            IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();
        parser.parse(ARQUIVO_DADOS, this);
        Assert.assertTrue(writer.numDocs() > 0);
    }
}