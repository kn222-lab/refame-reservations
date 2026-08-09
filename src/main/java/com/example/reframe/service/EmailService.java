package com.example.reframe.service;

import com.example.reframe.entity.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.admin.email}")
    private String adminEmail;

    // application.properties の値を注入
    @Value("${app.base-url}")
    private String baseUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日");

    /**
     * お客様宛ての予約確定メール送信
     */
    public void sendCustomerConfirmation(Reservation reservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(reservation.getCustomerEmail());
        message.setSubject("【Reframe】ご予約が完了いたしました");

        boolean isZushi = "逗子".equals(reservation.getLocationName());
        String formattedDate = reservation.getReservedDate().format(DATE_FORMATTER);

        StringBuilder body = new StringBuilder();
        body.append(reservation.getCustomerName()).append(" 様\n\n");
        body.append("Reframe 整体へのご予約ありがとうございます。\n");
        body.append("以下の内容でご予約を承りましたのでご確認ください。\n\n");

        body.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        body.append("■ ご予約内容\n");
        body.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        body.append("【ご予約日時】").append(formattedDate).append(" (土) ").append(reservation.getTimeSlot()).append("〜\n");
        body.append("【ご予約店舗】").append(reservation.getLocationName()).append("\n");
        body.append("【施術コース】整体 ＋ セルフケア指導コース (60分)\n");
        body.append("【お会計金額】8,000円 (税込)\n\n");

        // 予約確認
        body.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        body.append("■ ご予約の確認・キャンセル\n");
        body.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        body.append("ご予約内容の確認やキャンセルは、以下の専用URLから行っていただけます。\n");
        body.append(baseUrl).append("/reservation/confirm?code=").append(reservation.getReservationCode());

        //場所
        body.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        body.append("■ 店舗・アクセス情報\n");
        body.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        if (isZushi) {
            body.append("【逗子】\n");
            body.append("・最寄駅: 京急「神武寺駅」徒歩5分\n");
            body.append("・所在地: カルチェットスポーツクラブ事務所内\n");
            body.append("・駐車場: 【あり】敷地内の無料駐車場をご利用いただけます。\n");
            body.append("・Google Map: https://maps.app.goo.gl/ZErjpuhXea9QnhmQ9\n");
        } else {
            body.append("【由比ヶ浜】\n");
            body.append("・最寄駅: 江ノ島電鉄「和田塚駅」徒歩2分\n");
            body.append("・所在地: 神奈川県鎌倉市由比ガ浜2丁目7-21（由比ヶ浜公会堂）\n");
            body.append("・駐車場: 【なし】専用の駐車場はございません。\n");
            body.append("  ※お車でお越しの際は近隣のコインパーキングをご利用ください。\n");
            body.append("・駐輪場: 【あり】自転車やバイクでお越しいただけます。\n");
            body.append("・Google Map: https://maps.app.goo.gl/bCXeyZHcocpbN9XJA\n");
        }

        body.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        body.append("■ 当日のご案内・お願い\n");
        body.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        body.append("・当日はストレッチや運動を行いますので、動きやすい服装でお越しいただくか、お着替えをご持参ください。\n");
        body.append("・ご予約時間の5分ほど前を目安にお越しください。\n");
        body.append("・万が一、キャンセルや遅れてご到着される場合は、本メールへのご返信にてご連絡をお願いいたします。\n\n");

        body.append("当日のご来店を心よりお待ちしております。\n\n");
        body.append("----------------------------------------\n");
        body.append("Reframe / 整体\n");
        body.append("----------------------------------------\n");

        message.setText(body.toString());
        mailSender.send(message);
    }

    /**
     * 管理者宛ての予約通知メール送信
     */
    public void sendAdminNotification(Reservation reservation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(adminEmail);
        message.setSubject("【予約通知】" + reservation.getLocationName() + " - " + reservation.getCustomerName() + "様");

        String formattedDate = reservation.getReservedDate().format(DATE_FORMATTER);

        StringBuilder body = new StringBuilder();
        body.append("WEBサイトより新しい予約が入りました。\n\n");
        body.append("【店舗】").append(reservation.getLocationName()).append("\n");
        body.append("【日時】").append(formattedDate).append(" (土) ").append(reservation.getTimeSlot()).append("〜\n");
        body.append("【お名前】").append(reservation.getCustomerName()).append(" 様\n");
        body.append("【メール】").append(reservation.getCustomerEmail() != null && !reservation.getCustomerEmail().isBlank() ? reservation.getCustomerEmail() : "未入力").append("\n");
        body.append("【予約コード】").append(reservation.getReservationCode()).append("\n");

        message.setText(body.toString());
        mailSender.send(message);
    }
}