package org.refit.spring.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FCMConfig {
    @PostConstruct
    public void initialize() {
        try {
            //클래스패스에서 JSON 파일 읽기
            InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("kb-refit-firebase-adminsdk-fbsvc-179abaa384.json");

            if (serviceAccount == null) {
                throw new IllegalStateException("firebase json 키 파일을 찾을 수 없습니다.");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("FirebaseApp 초기화 완료");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
