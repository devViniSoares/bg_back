package com.bigodeautopecas.backend.config;

import com.bigodeautopecas.backend.model.Produto;
import com.bigodeautopecas.backend.model.Usuario;
import com.bigodeautopecas.backend.repository.ProdutoRepository;
import com.bigodeautopecas.backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:admin@bigodeautopecas.com}")
    private String adminEmail;

    @Value("${admin.senha:admin123}")
    private String adminSenha;

    @Value("${admin.nome:Administrador}")
    private String adminNome;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           ProdutoRepository produtoRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedAdmin();
        seedProdutos();
    }

    // ── Admin ─────────────────────────────────────────────────────────────────

    private void seedAdmin() {
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = new Usuario();
            admin.setNome(adminNome);
            admin.setEmail(adminEmail);
            admin.setSenha(passwordEncoder.encode(adminSenha));
            admin.setTipo("ADMIN");
            usuarioRepository.save(admin);
            log.info("Admin criado: {}", adminEmail);
        }
    }

    // ── Produtos ──────────────────────────────────────────────────────────────

    private void seedProdutos() {
        if (produtoRepository.count() > 0) {
            log.info("Produtos já existem no banco — seed ignorado.");
            return;
        }

        List<Produto> produtos = List.of(

            produto(
                "Molas Esportivas VW Gol G2/G3/G4 KIT Completo",
                "02 Molas Esportivas Dianteiras e 02 Molas Esportivas Traseiras",
                631.73, 5, "Suspensão", "Macaulay · Molas Esportivas", "Gol G2/G3/G4",
                "https://macaulay.com.br/_next/image?url=https%3A%2F%2Fmacaulay.com.br%2Fwp-content%2Fuploads%2F2023%2F09%2FMolas-Esportivas-MACAULAY-KIT-m1.jpg&w=1080&q=75"
            ),

            produto(
                "Farol Led Mercedes Gla 200 250 45 AMG",
                "Fabricada em Material de alta resistência\n" +
                "Excelente Acabamento\n" +
                "Com Lente Acrilico\n" +
                "Encaixe Sob Medida\n" +
                "Tecnologia LED\n" +
                "Não Acompanha Lâmpadas\n" +
                "Não Acompanha Reator\n" +
                "Aplicação: Dianteiro\n" +
                "Plug Contem 5 Pinos",
                13674.3, 4, "Iluminação", "Mercedes-benz", "Gla 200 250 45 AMG 2018 2019 2020",
                "https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcRbSlKd7yXvRYwmDNve5P1YdQZKgHtoSVaLIydkodpdQKhNfMr5AXYTOL87Tdtv5X7t8P3IfND1YX2ohNnYOtdm9NbuKuVUuw"
            ),

            produto(
                "Turbina .70 / t3 - com refluxo eixo 64mm rotor 59mm - refrigerada a água - a/r .63 monofluxo",
                "Turbina SPA27 / T3 com refluxo.\n\n" +
                "Rotor: 59mm desenvolvido com exclusividade pela SPA Turbo em alumínio billet com conceito EXTENDED TIP, " +
                "que aumenta o fluxo máximo do compressor, com aumento mínimo de massa, garantindo maior eficiência e fluxo " +
                "em relação aos concorrentes do mercado.\n\n" +
                "Eixo: 64mm.\n\n" +
                "Caixa Fria .70 com refluxo. Projeto especial SPA Turbo que otimiza o volume e fluxo de ar em conjunto com o rotor billet.\n\n" +
                "Faixa de potência recomendada: 350-600.\n\n" +
                "Cilindrada recomendada: 2.0 - 4.5.\n\n" +
                "Lubrificação: óleo. Refrigeração: óleo e água.\n\n" +
                "Balanceamento VSR executado em alta velocidade nas dependências da SPA Turbo.\n\n" +
                "Turbinas SPA Turbo — Especialista em Turbos desde 1990.",
                3704.85, 10, "Motor", "SPA Turbo", null,
                "https://spaturbo.vteximg.com.br/arquivos/ids/187702-1000-1000/BBSPA596463M--1-.jpg?v=637020878487100000"
            ),

            produto(
                "Disco de freio dianteiro Audi RS6 RS7 2010 a 2019",
                "Para garantir 100% de compatibilidade, verifique se os códigos originais abaixo correspondem à sua peça atual:\n\n" +
                "Marca: SHW Performance (Qualidade OEM)\n" +
                "Tipo: Wave / Flutuante / Ventilado\n" +
                "Posição: Dianteiro\n" +
                "Conteúdo: 01 Par (Dois Discos)\n" +
                "Código do Fabricante: AFX47415\n\n" +
                "Códigos Originais (VAG OEM):\n" +
                "4G0.615.301.AH | 4G0615301AH\n" +
                "4G0.615.301.E | 4G0615301E",
                22863.0, 15, "Freio", "SHW Performance", "RS6 RS7 2010 a 2019",
                "https://casteloimports.com.br/wp-content/uploads/2026/04/Gemini_Generated_Image_wkp2h8wkp2h8wkp2.png"
            ),

            produto(
                "Filtro Óleo Golf 1.6 8v 2006 2007 2008 2009 2010 2011 2012 Tecfil",
                "Conteúdo: Unitário\n" +
                "Modelo: BLINDADO\n" +
                "Comprimento: 84 mm\n" +
                "Elemento Filtrante: PAPEL\n" +
                "Diâmetro Externo: 75,7 mm\n" +
                "Tipo De Rosca Unificada: 3/4X16UNF-2B\n" +
                "Inclui válvula alivio: SIM\n" +
                "Inclui válvula anti-retorno: SIM",
                32.5, 26, "Motor", "Tecfil", "Golf 1.6 8v 2006 2007 2008 2009 2010 2011 2012",
                "https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcSf5wk0xDUqPsnkPg2DW4XGAZArHzrNJrY8rV1jo4RzObURmP9LAvKvAiNfwM0UsdmjoJXHkZCkybK09ZGKqYZnNhT4Qf1i_rTxtoeODPszhuJ-j4JPm6LG"
            ),

            produto(
                "COFAP Pastilha Freio Dianteira Gm Cobalt Onix Sonic Spin Sem Abs",
                "Conteúdo da Embalagem:\n" +
                "- 01 JOGO DE PASTILHAS DE FREIO DIANTEIRA - COFAP\n\n" +
                "APLICAÇÃO:\n" +
                "Novo Uno Evo / Firefly 2010 à 2021\n" +
                "ONIX 2013 à 2018\n" +
                "PRISMA 2013 à 2018\n" +
                "COBALT 2012 à 2016",
                70.0, 15, "Freio", "COFAP", "Cobalt Onix Sonic Spin Sem Abs",
                "https://m.media-amazon.com/images/I/61+xRNBUHbL._AC_SX522_.jpg"
            ),

            produto(
                "Lampada H4 Led Ultra Par Farol Alto Baixo Super Branca Xenom",
                "Tecnologia Alemã\n" +
                "Design NanoLed (Altamente compacta)\n" +
                "Tonalidade: 6500K Extremamente Branca\n" +
                "Resistente a Água\n" +
                "Base e Corpo da Lâmpada em Metal\n" +
                "Eficiência de 22.000 Lúmens do Par (11.000 Lúmens Cada)\n" +
                "Longa Vida Útil — Dura Até 3 Vezes Mais que LEDs Convencionais\n" +
                "Potência máxima de 36 Watts\n" +
                "Opera em 12V\n" +
                "Acompanha Fontes Embutidas\n" +
                "Chip Importado de Alta Potência",
                58.9, 44, "Iluminação", "Y3",
                "ASTRA 1995–1998 H4 / ASTRA 1999–2002 H7+H1+H3 / ASTRA 2003+ H7+H1+H3 / BLAZER 1995–2000 HB4+HB3+H3",
                "https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcSQy66VkOiDPWiiLaHhzCRqVLL9YIX_xjlsgR6T-tOcZgLKj4as8yAr8KuEMubBVVxbsHvklOMBCxQs5FVOcENxrckgNHlU8A"
            ),

            produto(
                "Par Barra Axial BMW 116i 118i 120i 130i 316i 318i 320i 320si X1 Sdrive 18i Sistema TRW Com Coifas",
                "Par Barra Axial Com Coifas\n\n" +
                "Aplica-se no lado direito e esquerdo para caixa de direção hidráulica sistema TRW.\n\n" +
                "APLICAÇÃO:\n" +
                "BMW 120i 2005–2012 Série E81 E82 E87 E88\n" +
                "BMW 116i 2003–2011 Série E81 E87\n" +
                "BMW 130i 2005–2012 Série E81 E87\n" +
                "BMW 320i 2007–2012 Série E90 E91 E92 E93\n" +
                "BMW 316i 2005–2011 Série E90 E91\n" +
                "BMW 118i 2007–2012 Série E81 E87 E88\n" +
                "BMW 320si 2005–2011 Série E90\n" +
                "BMW 318i 2007–2012 Série E90 E91\n" +
                "BMW X1 Sdrive 18i 2010–2014 Série E84\n\n" +
                "MEDIDA: 18mm × 245mm × 14mm\n\n" +
                "GARANTIA: 3 meses com nota fiscal\n\n" +
                "ITEM ANUNCIADO:\n" +
                "1× Par barras axiais\n" +
                "1× Par coifas\n" +
                "1× Par abraçadeiras de metal\n" +
                "1× Par abraçadeiras de plástico",
                275.05, 10, "Suspensão", "Rt suspensões",
                "BMW 116i 118i 120i 130i 316i 318i 320i 320si X1 Sdrive 18i",
                "https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcQt0n1FjiHK0qM1-c1BCECUiFW8f4tkYffwr4gf-fNtBGbGmuyNbbpJr4W_ldRNyyIKJGJmF9u49NPIQtbAHXes5Z0CKgUJ3Q"
            )
        );

        produtoRepository.saveAll(produtos);
        log.info("{} produtos inseridos com sucesso.", produtos.size());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Produto produto(String nome, String descricao, double preco, int estoque,
                            String categoria, String marca, String modelo, String imagemUrl) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setDescricao(descricao);
        p.setPreco(preco);
        p.setEstoque(estoque);
        p.setCategoria(categoria);
        p.setMarca(marca);
        p.setModelo(modelo);
        p.setImagemUrl(imagemUrl);
        return p;
    }
}
