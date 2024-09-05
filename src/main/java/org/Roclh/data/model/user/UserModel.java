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
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_model_gen")
    @SequenceGenerator(name = "user_model_gen", sequenceName = "user_model_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @NonNull
    @Column(unique = true)
    private String telegramId;
    @Nullable
    private String telegramName;
    @Nullable
    private String usedPort;
    @Nullable
    private String password;
    @Nullable
    private Long chatId;
    private boolean isAdded;

    @Override
    public String toString() {
        return "[" + telegramId + ":'" + telegramName + "']: " +
                " usedPort='" + usedPort + '\'' +
                ", isAdded=" + isAdded +
                ", password=" + password;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserModel userModel = (UserModel) o;
        return getId() != null && Objects.equals(getId(), userModel.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
