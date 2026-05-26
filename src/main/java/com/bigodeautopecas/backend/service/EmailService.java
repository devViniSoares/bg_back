package com.bigodeautopecas.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String from;

    public void enviarConfirmacaoPedido(String destinatario, Long pedidoId, Double total) {
        if (mailSender == null) {
            log.debug("E-mail não configurado — confirmação de pedido #{} não enviada", pedidoId);
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(destinatario);
            msg.setSubject("Pedido #" + pedidoId + " recebido — Bigode Auto Peças");
            msg.setText(
                "Olá!\n\n" +
                "Seu pedido #" + pedidoId + " foi recebido com sucesso.\n" +
                "Total: R$ " + String.format("%.2f", total) + "\n\n" +
                "Obrigado por comprar na Bigode Auto Peças!"
            );
            mailSender.send(msg);
            log.info("E-mail de confirmação enviado para {} (pedido #{})", destinatario, pedidoId);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de confirmação para {} (pedido #{}): {}", destinatario, pedidoId, e.getMessage());
        }
    }

    public void enviarConfirmacaoPagamento(String destinatario, Long pedidoId, String codigoTransacao) {
        if (mailSender == null) return;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(destinatario);
            msg.setSubject("Pagamento aprovado — Pedido #" + pedidoId);
            msg.setText(
                "Olá!\n\n" +
                "O pagamento do seu pedido #" + pedidoId + " foi aprovado.\n" +
                "Código da transação: " + codigoTransacao + "\n\n" +
                "Bigode Auto Peças"
            );
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de pagamento: {}", e.getMessage());
        }
    }

    public void enviarRedefinicaoSenha(String destinatario, String novaSenhaTemporaria) {
        if (mailSender == null) return;
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(destinatario);
            msg.setSubject("Sua senha foi alterada — Bigode Auto Peças");
            msg.setText(
                "Olá!\n\n" +
                "Sua senha foi alterada com sucesso.\n" +
                "Se não foi você, entre em contato imediatamente.\n\n" +
                "Bigode Auto Peças"
            );
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail de senha: {}", e.getMessage());
        }
    }
}