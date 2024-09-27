package org.Roclh.data.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.entities.UserModel;
import org.Roclh.ss.ShadowsocksProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServerSharingService {
    private final ShadowsocksProperties shadowsocksProperties;

    public String generateServerUrl(@NonNull UserModel userModel){
        return generateServerUrl(userModel, null, null);
    }
    @Nullable
    public String generateServerUrl(@NonNull UserModel userModel, String plugins, String pluginsOpts) {
        StringBuilder uriBuilder = new StringBuilder();
        uriBuilder.append("//").append(Base64.getEncoder().encodeToString((shadowsocksProperties.getDefaultMethod() + ":" + userModel.getPassword()).getBytes()));
        uriBuilder.append("@").append(shadowsocksProperties.getAddress()).append(":").append(userModel.getUsedPort());
        uriBuilder.append("/");
        if (plugins != null && !plugins.isBlank()) {
            uriBuilder.append("?plugin=").append(plugins);
            if (pluginsOpts != null && !pluginsOpts.isBlank()) {
                uriBuilder.append(pluginsOpts);
            }
        }
        URI uri = null;
        try {
            uri = new URI("ss", uriBuilder.toString(), userModel.getUserModel().getTelegramName() + ":" + userModel.getUserModel().getTelegramId());
        } catch (URISyntaxException e) {
            log.error("Failed to create URI string", e);
        }
        return uri == null ? null : uri.toString();
    }
    public BufferedImage generateServerUrlQrCode(@NonNull UserModel userModel){
        return generateServerUrlQrCode(userModel, null, null);
    }

    @Nullable
    public BufferedImage generateServerUrlQrCode(@NonNull UserModel userModel, String plugins, String pluginsOpt){
        String uri = generateServerUrl(userModel, plugins, pluginsOpt);
        if(uri == null){
            log.error("Generated URI is null!");
            return null;
        }
        try {
            return createQR(uri);
        } catch (WriterException e) {
            log.error("Failed to create QR code from URI {}", uri, e);
        }
        return null;
    }

    private BufferedImage createQR(@NonNull String data) throws WriterException {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix matrix = barcodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}
