package org.Roclh.data.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_model_gen")
    @SequenceGenerator(name = "user_model_gen", sequenceName = "user_model_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    private String telegramId;
    private String telegramName;
    private String usedPort;
    private String password;
    private boolean isAdded;
    @Override
    public String toString() {
        return "[" + telegramId + ":'" + telegramName + "']: " +
                " usedPort='" + usedPort + '\'' +
                ", isAdded=" + isAdded +
                ", password=" + password;
    }
}
