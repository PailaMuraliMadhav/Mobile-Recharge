package com.example.notificationservice.service;

import com.example.notificationservice.dto.RechargeEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/*
 * AUTHOR: Paila Murali Madhav
 * CLASS: NotificationService
 * DESCRIPTION:
 *   Service responsible for sending HTML email notifications to users after a recharge event.
 *   Builds success or failure email templates based on the recharge status and dispatches
 *   them via JavaMailSender. Skips notifications for PENDING status events.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /* ================================================================
     * METHOD: sendNotification
     * DESCRIPTION:
     *   Determines the appropriate email template based on the recharge event status
     *   and sends an HTML email to the user. Skips sending for PENDING status events
     *   and logs a warning if the user email is missing.
     * ================================================================ */
    public void sendNotification(RechargeEvent event) {
        if ("PENDING".equalsIgnoreCase(event.getStatus())) {
            log.info("Skipping notification for PENDING status — rechargeId={}", event.getId());
            return;
        }

        boolean isSuccess = "SUCCESS".equalsIgnoreCase(event.getStatus());

        String subject = isSuccess
                ? "✅ Recharge Successful — " + event.getOperatorName()
                : "❌ Recharge Failed — " + event.getOperatorName();

        String messageBody = isSuccess
                ? buildSuccessEmail(event)
                : buildFailureEmail(event);

        log.info("======= EMAIL NOTIFICATION =======");
        log.info("TO      : {}", event.getUserEmail());
        log.info("SUBJECT : {}", subject);
        log.info("STATUS  : {}", event.getStatus());
        log.info("==================================");

        if (StringUtils.hasText(event.getUserEmail())) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(fromEmail);
                helper.setTo(event.getUserEmail());
                helper.setSubject(subject);
                helper.setText(messageBody, true);
                mailSender.send(message);
                log.info("Email sent successfully to {}", event.getUserEmail());
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", event.getUserEmail(), e.getMessage());
            }
        } else {
            log.warn("Skipping email — userEmail is missing for rechargeId={}", event.getId());
        }
    }

    /* ================================================================
     * METHOD: buildSuccessEmail
     * DESCRIPTION:
     *   Constructs and returns an HTML email body for a successful recharge,
     *   including plan details, amount, validity, and transaction ID.
     * ================================================================ */
    private String buildSuccessEmail(RechargeEvent event) {
        return "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;border:1px solid #e0e0e0;border-radius:8px'>"
                + "<div style='background:#16a34a;padding:20px;border-radius:8px 8px 0 0;text-align:center'>"
                + "<h2 style='color:#fff;margin:0'>✅ Recharge Successful!</h2></div>"
                + "<div style='padding:24px'>"
                + "<p style='font-size:16px;color:#333'>Hi, your mobile recharge was successful.</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:16px 0'>"
                + row("Mobile Number", event.getMobileNumber())
                + row("Operator", event.getOperatorName() != null ? event.getOperatorName() : "—")
                + row("Plan", event.getPlanName() != null ? event.getPlanName() : "—")
                + row("Amount", "₹" + event.getAmount())
                + row("Data / Benefits", event.getPlanDescription() != null ? event.getPlanDescription() : "Standard benefits")
                + row("Validity", (event.getValidityDays() != null ? event.getValidityDays() : 28) + " days")
                + row("Transaction ID", event.getTransactionId() != null ? event.getTransactionId() : "N/A")
                + "</table>"
                + "<p style='color:#666;font-size:13px;margin-top:24px'>Thank you for using OmniRecharge. Your plan is now active.</p>"
                + "</div>"
                + "<div style='background:#f9f9f9;padding:12px;text-align:center;border-radius:0 0 8px 8px'>"
                + "<p style='color:#999;font-size:12px;margin:0'>OmniRecharge — Instant Mobile Recharges</p>"
                + "</div></div>";
    }

    /* ================================================================
     * METHOD: buildFailureEmail
     * DESCRIPTION:
     *   Constructs and returns an HTML email body for a failed recharge,
     *   including plan details and a refund notice for any deducted amount.
     * ================================================================ */
    private String buildFailureEmail(RechargeEvent event) {
        return "<div style='font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;border:1px solid #e0e0e0;border-radius:8px'>"
                + "<div style='background:#dc2626;padding:20px;border-radius:8px 8px 0 0;text-align:center'>"
                + "<h2 style='color:#fff;margin:0'>❌ Recharge Failed</h2></div>"
                + "<div style='padding:24px'>"
                + "<p style='font-size:16px;color:#333'>Unfortunately, your recharge could not be processed.</p>"
                + "<table style='width:100%;border-collapse:collapse;margin:16px 0'>"
                + row("Mobile Number", event.getMobileNumber())
                + row("Operator", event.getOperatorName() != null ? event.getOperatorName() : "—")
                + row("Plan", event.getPlanName() != null ? event.getPlanName() : "—")
                + row("Amount", "₹" + event.getAmount())
                + row("Transaction ID", event.getTransactionId() != null ? event.getTransactionId() : "N/A")
                + "</table>"
                + "<div style='background:#fef2f2;border:1px solid #fecaca;border-radius:6px;padding:12px;margin:16px 0'>"
                + "<p style='color:#991b1b;margin:0;font-size:14px'>💡 <strong>Refund Note:</strong> "
                + "If any amount was deducted, it will be automatically refunded within 3-5 business days.</p>"
                + "</div>"
                + "<p style='color:#666;font-size:13px'>Please try again or contact support if the issue persists.</p>"
                + "</div>"
                + "<div style='background:#f9f9f9;padding:12px;text-align:center;border-radius:0 0 8px 8px'>"
                + "<p style='color:#999;font-size:12px;margin:0'>OmniRecharge — Instant Mobile Recharges</p>"
                + "</div></div>";
    }

    /* ================================================================
     * METHOD: row
     * DESCRIPTION:
     *   Helper method that generates an HTML table row with a label cell and a value cell,
     *   used to build the recharge details table in email templates.
     * ================================================================ */
    private String row(String label, String value) {
        return "<tr>"
                + "<td style='padding:8px 12px;background:#f9f9f9;border:1px solid #e5e7eb;font-weight:600;color:#374151;width:40%'>" + label + "</td>"
                + "<td style='padding:8px 12px;border:1px solid #e5e7eb;color:#111827'>" + value + "</td>"
                + "</tr>";
    }
}
