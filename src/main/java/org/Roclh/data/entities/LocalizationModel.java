package org.Roclh.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizationModel {
    @Id
    @NonNull
    @Column(name = "telegramId", nullable = false)
    private Long telegramId;
    @NonNull
    private String locale;
}
